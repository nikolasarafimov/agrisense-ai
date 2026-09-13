"""Load and validate ML artifacts used by the prediction service."""

from __future__ import annotations

import logging
from dataclasses import dataclass
from pathlib import Path
from typing import Any

import joblib
import numpy as np
from sklearn.pipeline import Pipeline


logger = logging.getLogger(__name__)


ARTIFACTS_DIR = (
    Path(__file__).resolve().parent.parent
    / "trained_model_objects"
)

MODEL_PATH = (
    ARTIFACTS_DIR
    / "irrigation_model.pkl"
)

PREPROCESSOR_PATH = (
    ARTIFACTS_DIR
    / "preprocessor.pkl"
)

LABEL_ENCODER_PATH = (
    ARTIFACTS_DIR
    / "label_encoder.pkl"
)

THRESHOLD_PATH = (
    ARTIFACTS_DIR
    / "healthy_soil_moisture_threshold.pkl"
)


@dataclass(frozen=True)
class Artifacts:
    model: Any
    preprocessor: Any
    label_encoder: Any
    healthy_soil_moisture_threshold: float


_artifacts: Artifacts | None = None


def _require_artifact(
    path: Path,
) -> None:
    if not path.is_file():
        raise FileNotFoundError(
            f"Missing required ML artifact: {path.name}",
        )


def _coerce_threshold(
    value: Any,
) -> float:
    if isinstance(value, dict):
        threshold_value = None

        for key in (
            "healthy_soil_moisture_threshold",
            "moisture_threshold",
        ):
            if (
                key in value
                and value[key] is not None
            ):
                threshold_value = value[key]
                break

        if threshold_value is None:
            raise ValueError(
                "Soil moisture threshold artifact "
                "does not contain a supported key.",
            )

        value = threshold_value

    try:
        threshold = float(
            np.asarray(value).item(),
        )
    except (TypeError, ValueError) as exc:
        raise ValueError(
            "Healthy soil moisture threshold "
            "must contain one numeric value.",
        ) from exc

    if not np.isfinite(threshold):
        raise ValueError(
            "Healthy soil moisture threshold "
            "must be finite.",
        )

    return threshold


def _load_classifier(
    model_path: Path,
) -> Any:
    loaded_model =
        joblib.load(
            model_path,
        )

    if (
        isinstance(
            loaded_model,
            Pipeline,
        )
        and "model"
        in loaded_model.named_steps
    ):
        logger.info(
            "Using classifier from Pipeline "
            "named step 'model'.",
        )

        return (
            loaded_model
            .named_steps["model"]
        )

    return loaded_model


def _validate_artifacts(
    model: Any,
    preprocessor: Any,
    label_encoder: Any,
) -> None:
    if not callable(
        getattr(
            model,
            "predict",
            None,
        ),
    ):
        raise TypeError(
            "Irrigation model artifact "
            "does not provide predict().",
        )

    if not callable(
        getattr(
            preprocessor,
            "transform",
            None,
        ),
    ):
        raise TypeError(
            "Preprocessor artifact "
            "does not provide transform().",
        )

    if not callable(
        getattr(
            label_encoder,
            "inverse_transform",
            None,
        ),
    ):
        raise TypeError(
            "Label encoder artifact "
            "does not provide inverse_transform().",
        )


def load_artifacts_into_memory() -> Artifacts:
    """
    Load all required ML artifacts and store them
    as a process-wide singleton.
    """
    global _artifacts

    required_paths = (
        MODEL_PATH,
        PREPROCESSOR_PATH,
        LABEL_ENCODER_PATH,
        THRESHOLD_PATH,
    )

    for path in required_paths:
        _require_artifact(
            path,
        )

    model =
        _load_classifier(
            MODEL_PATH,
        )

    preprocessor =
        joblib.load(
            PREPROCESSOR_PATH,
        )

    label_encoder =
        joblib.load(
            LABEL_ENCODER_PATH,
        )

    threshold_raw =
        joblib.load(
            THRESHOLD_PATH,
        )

    threshold =
        _coerce_threshold(
            threshold_raw,
        )

    _validate_artifacts(
        model,
        preprocessor,
        label_encoder,
    )

    loaded_artifacts =
        Artifacts(
            model=model,
            preprocessor=preprocessor,
            label_encoder=label_encoder,
            healthy_soil_moisture_threshold=threshold,
        )

    _artifacts =
        loaded_artifacts

    logger.info(
        "ML artifacts loaded successfully "
        "(healthy_soil_moisture_threshold=%s).",
        threshold,
    )

    return loaded_artifacts


def get_artifacts() -> Artifacts:
    if _artifacts is None:
        raise RuntimeError(
            "ML artifacts are not loaded. "
            "Application startup did not complete.",
        )

    return _artifacts