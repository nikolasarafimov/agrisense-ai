"""FastAPI service for AgriSense AI irrigation predictions."""

from __future__ import annotations

import logging
from contextlib import asynccontextmanager
from typing import Any

import numpy as np
from fastapi import (
    FastAPI,
    HTTPException,
    Request,
)
from fastapi.encoders import jsonable_encoder
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from pydantic import ValidationError

from app.feature_engineering import add_engineered_features
from app.model_loader import (
    Artifacts,
    get_artifacts,
    load_artifacts_into_memory,
)
from app.schemas import (
    PredictionItem,
    PredictionResponse,
    PredictRequestBody,
    SinglePredictionRequest,
)
from app.utils import (
    records_to_raw_dataframe,
    reorder_features,
    summarize_batch_for_log,
)


logging.basicConfig(
    level=logging.INFO,
    format=(
        "%(asctime)s %(levelname)s "
        "[%(name)s] %(message)s"
    ),
)

logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(_app: FastAPI):
    logger.info(
        "Loading irrigation prediction artifacts.",
    )

    load_artifacts_into_memory()

    logger.info(
        "Irrigation prediction artifacts loaded successfully.",
    )

    yield


app = FastAPI(
    title="AgriSense AI ML Service",
    description=(
        "Machine learning service for irrigation "
        "recommendations used by the AgriSense AI backend."
    ),
    version="1.0.0",
    lifespan=lifespan,
)


@app.exception_handler(RequestValidationError)
async def request_validation_handler(
    _request: Request,
    exc: RequestValidationError,
) -> JSONResponse:
    logger.warning(
        "Request validation failed: %s",
        exc.errors(),
    )

    return JSONResponse(
        status_code=400,
        content=jsonable_encoder(
            {
                "detail": exc.errors(),
            },
        ),
    )


@app.exception_handler(ValidationError)
async def pydantic_validation_handler(
    _request: Request,
    exc: ValidationError,
) -> JSONResponse:
    logger.warning(
        "Validation error: %s",
        exc.errors(),
    )

    return JSONResponse(
        status_code=400,
        content=jsonable_encoder(
            {
                "detail": exc.errors(),
            },
        ),
    )


@app.get("/health")
def health() -> dict[str, str]:
    return {
        "status": "UP",
        "service": "agrisense-ml",
    }


def _normalize_to_batch(
    body: PredictRequestBody,
) -> list[dict[str, Any]]:
    if isinstance(
        body,
        SinglePredictionRequest,
    ):
        return [
            body.model_dump(),
        ]

    if not body:
        raise HTTPException(
            status_code=400,
            detail={
                "message":
                    "Batch must contain at least one record.",
            },
        )

    return [
        row.model_dump()
        for row in body
    ]


def _class_names_for_proba(
    model: Any,
    label_encoder: Any,
    n_cols: int,
) -> list[str]:
    model_classes =
        getattr(
            model,
            "classes_",
            None,
        )

    if (
        model_classes is not None
        and len(model_classes) == n_cols
    ):
        classes =
            np.asarray(
                model_classes,
            ).ravel()

        if np.issubdtype(
            classes.dtype,
            np.integer,
        ):
            decoded =
                label_encoder.inverse_transform(
                    classes.astype(
                        int,
                        copy=False,
                    ),
                )

            return [
                str(value)
                for value in decoded
            ]

        return [
            str(value)
            for value in classes
        ]

    encoder_classes =
        getattr(
            label_encoder,
            "classes_",
            None,
        )

    if (
        encoder_classes is not None
        and len(encoder_classes) == n_cols
    ):
        return [
            str(value)
            for value in encoder_classes
        ]

    raise ValueError(
        "Could not align probability columns "
        "with prediction class names.",
    )


def _build_probability_maps(
    model: Any,
    label_encoder: Any,
    probabilities: np.ndarray,
) -> list[dict[str, float]]:
    if probabilities.ndim != 2:
        raise ValueError(
            "predict_proba returned an unexpected shape.",
        )

    class_names =
        _class_names_for_proba(
            model,
            label_encoder,
            probabilities.shape[1],
        )

    return [
        {
            class_names[index]:
                float(row[index])
            for index
            in range(len(class_names))
        }
        for row in probabilities
    ]


def _decode_predictions(
    predictions: Any,
    label_encoder: Any,
) -> list[str]:
    flattened =
        np.asarray(
            predictions,
        ).ravel()

    if np.issubdtype(
        flattened.dtype,
        np.integer,
    ):
        decoded =
            label_encoder.inverse_transform(
                flattened.astype(
                    int,
                    copy=False,
                ),
            )

        return [
            str(value)
            for value in decoded
        ]

    return [
        str(value)
        for value in flattened
    ]


def _predict_batch(
    artifacts: Artifacts,
    rows: list[dict[str, Any]],
) -> list[PredictionItem]:
    raw_dataframe =
        records_to_raw_dataframe(
            rows,
        )

    engineered_dataframe =
        add_engineered_features(
            raw_dataframe,
            artifacts
                .healthy_soil_moisture_threshold,
        )

    feature_dataframe =
        reorder_features(
            engineered_dataframe,
        )

    try:
        transformed_features =
            artifacts.preprocessor.transform(
                feature_dataframe,
            )

    except Exception as exc:
        logger.exception(
            "Preprocessor transform failed.",
        )

        raise HTTPException(
            status_code=400,
            detail={
                "message":
                    "Input data could not be preprocessed.",
            },
        ) from exc

    try:
        raw_predictions =
            artifacts.model.predict(
                transformed_features,
            )

        labels =
            _decode_predictions(
                raw_predictions,
                artifacts.label_encoder,
            )

    except Exception as exc:
        logger.exception(
            "Model prediction failed.",
        )

        raise HTTPException(
            status_code=500,
            detail={
                "message":
                    "Prediction could not be generated.",
            },
        ) from exc

    probability_maps: list[
        dict[str, float] | None
    ]

    if hasattr(
        artifacts.model,
        "predict_proba",
    ):
        try:
            probabilities =
                np.asarray(
                    artifacts.model
                        .predict_proba(
                            transformed_features,
                        ),
                    dtype=float,
                )

            probability_maps =
                _build_probability_maps(
                    artifacts.model,
                    artifacts.label_encoder,
                    probabilities,
                )

        except Exception:
            logger.warning(
                "Probability calculation failed; "
                "returning prediction labels only.",
                exc_info=True,
            )

            probability_maps = [
                None
            ] * len(labels)

    else:
        probability_maps = [
            None
        ] * len(labels)

    return [
        PredictionItem(
            label=label,
            probabilities=
                probability_maps[index],
        )
        for index, label
        in enumerate(labels)
    ]


@app.post(
    "/predict",
    response_model=PredictionResponse,
    response_model_exclude_none=True,
)
def predict(
    body: PredictRequestBody,
) -> PredictionResponse:
    rows =
        _normalize_to_batch(
            body,
        )

    logger.info(
        "POST /predict request received: %s",
        summarize_batch_for_log(
            len(rows),
            rows[0].keys(),
        ),
    )

    artifacts =
        get_artifacts()

    predictions =
        _predict_batch(
            artifacts,
            rows,
        )

    logger.info(
        "Prediction succeeded batch_size=%s",
        len(predictions),
    )

    return PredictionResponse(
        predictions=predictions,
    )