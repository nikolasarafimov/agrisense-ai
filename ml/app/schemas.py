"""Pydantic request and response schemas for irrigation prediction."""

from __future__ import annotations

import math
from typing import Any

from pydantic import (
    BaseModel,
    ConfigDict,
    Field,
    ValidationInfo,
    field_validator,
    model_validator,
)


def _finite_float(
    name: str,
    value: Any,
) -> float:
    if (
        not isinstance(
            value,
            (int, float),
        )
        or isinstance(
            value,
            bool,
        )
    ):
        raise ValueError(
            f"{name} must be a number.",
        )

    float_value = float(
        value,
    )

    if not math.isfinite(
        float_value,
    ):
        raise ValueError(
            f"{name} must be a finite number.",
        )

    return float_value


class SinglePredictionRequest(BaseModel):
    """One record containing the raw features required by the model."""

    model_config = ConfigDict(
        extra="forbid",
        str_strip_whitespace=True,
    )

    Soil_pH: float
    Soil_Moisture: float
    Organic_Carbon: float
    Electrical_Conductivity: float
    Temperature_C: float
    Humidity: float
    Rainfall_mm: float
    Sunlight_Hours: float
    Wind_Speed_kmh: float
    Field_Area_hectare: float
    Previous_Irrigation_mm: float

    Soil_Type: str = Field(
        ...,
        min_length=1,
    )

    Crop_Type: str = Field(
        ...,
        min_length=1,
    )

    Crop_Growth_Stage: str = Field(
        ...,
        min_length=1,
    )

    Season: str = Field(
        ...,
        min_length=1,
    )

    Irrigation_Type: str = Field(
        ...,
        min_length=1,
    )

    Water_Source: str = Field(
        ...,
        min_length=1,
    )

    Mulching_Used: str = Field(
        ...,
        min_length=1,
    )

    Region: str = Field(
        ...,
        min_length=1,
    )


    @field_validator(
        "Soil_pH",
        "Soil_Moisture",
        "Organic_Carbon",
        "Electrical_Conductivity",
        "Temperature_C",
        "Humidity",
        "Rainfall_mm",
        "Sunlight_Hours",
        "Wind_Speed_kmh",
        "Field_Area_hectare",
        "Previous_Irrigation_mm",
        mode="before",
    )
    @classmethod
    def validate_numeric(
        cls,
        value: Any,
        info: ValidationInfo,
    ) -> float:
        return _finite_float(
            info.field_name,
            value,
        )


    @field_validator(
        "Soil_Type",
        "Crop_Type",
        "Crop_Growth_Stage",
        "Season",
        "Irrigation_Type",
        "Water_Source",
        "Mulching_Used",
        "Region",
        mode="before",
    )
    @classmethod
    def validate_categorical(
        cls,
        value: Any,
    ) -> str:
        if not isinstance(
            value,
            str,
        ):
            raise ValueError(
                "Categorical features must be strings.",
            )

        normalized_value = (
            value.strip()
        )

        if not normalized_value:
            raise ValueError(
                "Categorical features must not be empty.",
            )

        return normalized_value


    @model_validator(
        mode="after",
    )
    def validate_non_negative_physical_values(
        self,
    ) -> "SinglePredictionRequest":
        for field_name in (
            "Humidity",
            "Rainfall_mm",
            "Sunlight_Hours",
            "Wind_Speed_kmh",
            "Field_Area_hectare",
            "Previous_Irrigation_mm",
        ):
            if (
                getattr(
                    self,
                    field_name,
                )
                < 0
            ):
                raise ValueError(
                    f"{field_name} must be greater than or equal to 0.",
                )

        return self


class PredictionItem(BaseModel):
    label: str
    probabilities: (
        dict[str, float]
        | None
    ) = None


class PredictionResponse(BaseModel):
    predictions: list[
        PredictionItem
    ]


PredictRequestBody = (
    SinglePredictionRequest
    | list[SinglePredictionRequest]
)