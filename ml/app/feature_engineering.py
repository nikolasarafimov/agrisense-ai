"""Feature engineering utilities for irrigation prediction."""

from __future__ import annotations

import numpy as np
import pandas as pd


_ENGINEERED_COLS = (
    "Moisture_Deficit",
    "Water_Availability",
    "ET_Proxy",
    "Irrigation_per_Hectare",
)

_EPSILON = 1e-5


def add_engineered_features(
    df: pd.DataFrame,
    healthy_soil_moisture_threshold: float,
) -> pd.DataFrame:
    """
    Add the engineered features used during model training.

    Engineered features:
    - Moisture_Deficit =
      max(healthy_soil_moisture_threshold - Soil_Moisture, 0)
    - Water_Availability =
      Rainfall_mm + Previous_Irrigation_mm
    - ET_Proxy =
      (Temperature_C * Wind_Speed_kmh) / (Humidity + 1e-5)
    - Irrigation_per_Hectare =
      Previous_Irrigation_mm / (Field_Area_hectare + 1e-5)
    """
    out = df.copy()

    out["Moisture_Deficit"] = (
        healthy_soil_moisture_threshold
        - out["Soil_Moisture"]
    ).clip(
        lower=0,
    )

    out["Water_Availability"] = (
        out["Rainfall_mm"]
        + out["Previous_Irrigation_mm"]
    )

    out["ET_Proxy"] = (
        out["Temperature_C"]
        * out["Wind_Speed_kmh"]
    ) / (
        out["Humidity"]
        + _EPSILON
    )

    out["Irrigation_per_Hectare"] = (
        out["Previous_Irrigation_mm"]
        / (
            out["Field_Area_hectare"]
            + _EPSILON
        )
    )

    for column in _ENGINEERED_COLS:
        out[column] = (
            out[column]
            .replace(
                [np.inf, -np.inf],
                np.nan,
            )
            .fillna(0.0)
        )

    return out