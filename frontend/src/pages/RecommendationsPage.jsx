import { useState } from "react";

import {
    apiRequest,
} from "../api";


const initialFormData = {
    Soil_pH: "6.8",
    Soil_Moisture: "32",
    Organic_Carbon: "1.2",
    Electrical_Conductivity: "0.8",
    Temperature_C: "32",
    Humidity: "35",
    Rainfall_mm: "2",
    Sunlight_Hours: "8",
    Wind_Speed_kmh: "12",
    Field_Area_hectare: "1.5",
    Previous_Irrigation_mm: "5",
    Soil_Type: "Loamy",
    Crop_Type: "Wheat",
    Crop_Growth_Stage: "Vegetative",
    Season: "Kharif",
    Irrigation_Type: "Drip",
    Water_Source: "Groundwater",
    Mulching_Used: "Yes",
    Region: "Central",
};


const numericFields = new Set([
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
]);


const nonNegativeNumericFields = new Set([
    "Soil_Moisture",
    "Organic_Carbon",
    "Electrical_Conductivity",
    "Humidity",
    "Rainfall_mm",
    "Sunlight_Hours",
    "Wind_Speed_kmh",
    "Field_Area_hectare",
    "Previous_Irrigation_mm",
]);


const soilTypes = [
    "Clay",
    "Loamy",
    "Sandy",
    "Silt",
];


const cropTypes = [
    "Cotton",
    "Maize",
    "Potato",
    "Rice",
    "Sugarcane",
    "Wheat",
];


const cropGrowthStages = [
    "Sowing",
    "Vegetative",
    "Flowering",
    "Harvest",
];


const seasons = [
    "Rabi",
    "Kharif",
    "Zaid",
];


const irrigationTypes = [
    "Canal",
    "Drip",
    "Rainfed",
    "Sprinkler",
];


const waterSources = [
    "Groundwater",
    "Rainwater",
    "Reservoir",
    "River",
];


const mulchingOptions = [
    "Yes",
    "No",
];


const regions = [
    "Central",
    "East",
    "North",
    "South",
    "West",
];


function RecommendationValue({
                                 label,
                                 value,
                             }) {
    return (
        <div>
            <span>
                {label}
            </span>

            <strong>
                {value}
            </strong>
        </div>
    );
}


function SelectField({
                         id,
                         label,
                         value,
                         options,
                         disabled,
                         onChange,
                     }) {
    return (
        <div className="form-field">
            <label htmlFor={id}>
                {label}
            </label>

            <select
                id={id}
                name={id}
                value={value}
                disabled={disabled}
                onChange={onChange}
                required
            >
                {options.map(
                    (option) => (
                        <option
                            key={option}
                            value={option}
                        >
                            {option}
                        </option>
                    ),
                )}
            </select>
        </div>
    );
}


export default function RecommendationsPage() {
    const [formData, setFormData] =
        useState(initialFormData);

    const [recommendation, setRecommendation] =
        useState(null);

    const [status, setStatus] =
        useState({
            loading: false,
            message: "",
            type: "",
        });


    const handleChange = (event) => {
        const {
            name,
            value,
        } = event.target;

        setFormData(
            (previousData) => ({
                ...previousData,
                [name]: value,
            }),
        );
    };


    const buildPayload = () => {
        const payload = {};

        for (
            const [key, value]
            of Object.entries(formData)
            ) {
            if (numericFields.has(key)) {
                if (
                    value === ""
                    || value === null
                    || value === undefined
                ) {
                    throw new Error(
                        "Please fill in all numeric fields.",
                    );
                }

                const numericValue =
                    Number(value);

                if (
                    !Number.isFinite(
                        numericValue,
                    )
                ) {
                    throw new Error(
                        "All numeric fields must contain valid numbers.",
                    );
                }

                if (
                    nonNegativeNumericFields.has(key)
                    && numericValue < 0
                ) {
                    throw new Error(
                        "Moisture, carbon, conductivity, humidity, rainfall, sunlight, wind speed, field area, and previous irrigation values cannot be negative.",
                    );
                }

                payload[key] =
                    numericValue;

                continue;
            }

            const textValue =
                String(value).trim();

            if (!textValue) {
                throw new Error(
                    "Please fill in all agricultural category fields.",
                );
            }

            payload[key] =
                textValue;
        }

        return payload;
    };


    const handleSubmit = async (event) => {
        event.preventDefault();

        let payload;

        try {
            payload =
                buildPayload();

        } catch (error) {
            setStatus({
                loading: false,
                message:
                    error.message
                    || "Please check the entered values.",
                type: "error",
            });

            return;
        }

        setStatus({
            loading: true,
            message:
                "Generating AI-supported recommendation...",
            type: "info",
        });

        setRecommendation(null);

        try {
            const response =
                await apiRequest(
                    "/api/recommendations",
                    {
                        method: "POST",
                        body:
                            JSON.stringify(
                                payload,
                            ),
                    },
                );

            setRecommendation(
                response,
            );

            setStatus({
                loading: false,
                message:
                    response.mlServiceAvailable
                        ? "ML recommendation generated successfully."
                        : "Fallback recommendation generated because the ML service was unavailable.",
                type:
                    response.mlServiceAvailable
                        ? "success"
                        : "info",
            });

        } catch (error) {
            setStatus({
                loading: false,
                message:
                    error.message
                    || "Could not generate recommendation.",
                type: "error",
            });
        }
    };


    const resetForm = () => {
        if (status.loading) {
            return;
        }

        setFormData(
            initialFormData,
        );

        setRecommendation(null);

        setStatus({
            loading: false,
            message: "",
            type: "",
        });
    };


    const predictionLabel =
        recommendation?.predictionLabel
        || recommendation?.predictions?.[0]?.label
        || recommendation?.prediction
        || recommendation?.result
        || "-";


    const probabilities =
        recommendation?.probabilities
        || recommendation?.predictions?.[0]?.probabilities
        || null;


    return (
        <main className="page-shell">
            <section className="page-header-card">
                <span className="section-label">
                    AgriSense AI Recommendation Engine
                </span>

                <h1>
                    AI-Supported Irrigation Recommendations
                </h1>

                <p>
                    Enter soil, field, weather, and crop
                    conditions to generate an irrigation
                    recommendation using the FastAPI
                    machine learning service. Agricultural
                    categories are limited to values
                    represented in the model training data.
                    If the ML service is unavailable, the
                    backend provides a fallback recommendation.
                </p>
            </section>


            <section className="form-layout">
                <form
                    className="data-form"
                    onSubmit={handleSubmit}
                >
                    <h2>
                        Soil and Field Conditions
                    </h2>


                    <div className="form-field">
                        <label htmlFor="Soil_pH">
                            Soil pH
                        </label>

                        <input
                            id="Soil_pH"
                            name="Soil_pH"
                            type="number"
                            step="any"
                            value={
                                formData.Soil_pH
                            }
                            disabled={
                                status.loading
                            }
                            onChange={
                                handleChange
                            }
                            required
                        />
                    </div>


                    <div className="form-field">
                        <label htmlFor="Soil_Moisture">
                            Soil Moisture
                        </label>

                        <input
                            id="Soil_Moisture"
                            name="Soil_Moisture"
                            type="number"
                            min="0"
                            step="any"
                            value={
                                formData.Soil_Moisture
                            }
                            disabled={
                                status.loading
                            }
                            onChange={
                                handleChange
                            }
                            required
                        />
                    </div>


                    <div className="form-field">
                        <label htmlFor="Organic_Carbon">
                            Organic Carbon
                        </label>

                        <input
                            id="Organic_Carbon"
                            name="Organic_Carbon"
                            type="number"
                            min="0"
                            step="any"
                            value={
                                formData.Organic_Carbon
                            }
                            disabled={
                                status.loading
                            }
                            onChange={
                                handleChange
                            }
                            required
                        />
                    </div>


                    <div className="form-field">
                        <label htmlFor="Electrical_Conductivity">
                            Electrical Conductivity
                        </label>

                        <input
                            id="Electrical_Conductivity"
                            name="Electrical_Conductivity"
                            type="number"
                            min="0"
                            step="any"
                            value={
                                formData.Electrical_Conductivity
                            }
                            disabled={
                                status.loading
                            }
                            onChange={
                                handleChange
                            }
                            required
                        />
                    </div>


                    <div className="form-field">
                        <label htmlFor="Field_Area_hectare">
                            Field Area (hectares)
                        </label>

                        <input
                            id="Field_Area_hectare"
                            name="Field_Area_hectare"
                            type="number"
                            min="0"
                            step="any"
                            value={
                                formData.Field_Area_hectare
                            }
                            disabled={
                                status.loading
                            }
                            onChange={
                                handleChange
                            }
                            required
                        />
                    </div>


                    <h2>
                        Weather Conditions
                    </h2>


                    <div className="form-field">
                        <label htmlFor="Temperature_C">
                            Temperature (°C)
                        </label>

                        <input
                            id="Temperature_C"
                            name="Temperature_C"
                            type="number"
                            step="any"
                            value={
                                formData.Temperature_C
                            }
                            disabled={
                                status.loading
                            }
                            onChange={
                                handleChange
                            }
                            required
                        />
                    </div>


                    <div className="form-field">
                        <label htmlFor="Humidity">
                            Humidity (%)
                        </label>

                        <input
                            id="Humidity"
                            name="Humidity"
                            type="number"
                            min="0"
                            step="any"
                            value={
                                formData.Humidity
                            }
                            disabled={
                                status.loading
                            }
                            onChange={
                                handleChange
                            }
                            required
                        />
                    </div>


                    <div className="form-field">
                        <label htmlFor="Rainfall_mm">
                            Rainfall (mm)
                        </label>

                        <input
                            id="Rainfall_mm"
                            name="Rainfall_mm"
                            type="number"
                            min="0"
                            step="any"
                            value={
                                formData.Rainfall_mm
                            }
                            disabled={
                                status.loading
                            }
                            onChange={
                                handleChange
                            }
                            required
                        />
                    </div>


                    <div className="form-field">
                        <label htmlFor="Sunlight_Hours">
                            Sunlight Hours
                        </label>

                        <input
                            id="Sunlight_Hours"
                            name="Sunlight_Hours"
                            type="number"
                            min="0"
                            step="any"
                            value={
                                formData.Sunlight_Hours
                            }
                            disabled={
                                status.loading
                            }
                            onChange={
                                handleChange
                            }
                            required
                        />
                    </div>


                    <div className="form-field">
                        <label htmlFor="Wind_Speed_kmh">
                            Wind Speed (km/h)
                        </label>

                        <input
                            id="Wind_Speed_kmh"
                            name="Wind_Speed_kmh"
                            type="number"
                            min="0"
                            step="any"
                            value={
                                formData.Wind_Speed_kmh
                            }
                            disabled={
                                status.loading
                            }
                            onChange={
                                handleChange
                            }
                            required
                        />
                    </div>


                    <div className="form-field">
                        <label htmlFor="Previous_Irrigation_mm">
                            Previous Irrigation (mm)
                        </label>

                        <input
                            id="Previous_Irrigation_mm"
                            name="Previous_Irrigation_mm"
                            type="number"
                            min="0"
                            step="any"
                            value={
                                formData.Previous_Irrigation_mm
                            }
                            disabled={
                                status.loading
                            }
                            onChange={
                                handleChange
                            }
                            required
                        />
                    </div>


                    <h2>
                        Agricultural Categories
                    </h2>


                    <SelectField
                        id="Soil_Type"
                        label="Soil Type"
                        value={
                            formData.Soil_Type
                        }
                        options={
                            soilTypes
                        }
                        disabled={
                            status.loading
                        }
                        onChange={
                            handleChange
                        }
                    />


                    <SelectField
                        id="Crop_Type"
                        label="Crop Type"
                        value={
                            formData.Crop_Type
                        }
                        options={
                            cropTypes
                        }
                        disabled={
                            status.loading
                        }
                        onChange={
                            handleChange
                        }
                    />


                    <SelectField
                        id="Crop_Growth_Stage"
                        label="Crop Growth Stage"
                        value={
                            formData.Crop_Growth_Stage
                        }
                        options={
                            cropGrowthStages
                        }
                        disabled={
                            status.loading
                        }
                        onChange={
                            handleChange
                        }
                    />


                    <SelectField
                        id="Season"
                        label="Season"
                        value={
                            formData.Season
                        }
                        options={
                            seasons
                        }
                        disabled={
                            status.loading
                        }
                        onChange={
                            handleChange
                        }
                    />


                    <SelectField
                        id="Irrigation_Type"
                        label="Irrigation Type"
                        value={
                            formData.Irrigation_Type
                        }
                        options={
                            irrigationTypes
                        }
                        disabled={
                            status.loading
                        }
                        onChange={
                            handleChange
                        }
                    />


                    <SelectField
                        id="Water_Source"
                        label="Water Source"
                        value={
                            formData.Water_Source
                        }
                        options={
                            waterSources
                        }
                        disabled={
                            status.loading
                        }
                        onChange={
                            handleChange
                        }
                    />


                    <SelectField
                        id="Mulching_Used"
                        label="Mulching Used"
                        value={
                            formData.Mulching_Used
                        }
                        options={
                            mulchingOptions
                        }
                        disabled={
                            status.loading
                        }
                        onChange={
                            handleChange
                        }
                    />


                    <SelectField
                        id="Region"
                        label="Region"
                        value={
                            formData.Region
                        }
                        options={
                            regions
                        }
                        disabled={
                            status.loading
                        }
                        onChange={
                            handleChange
                        }
                    />


                    <div className="form-actions-row">
                        <button
                            className="submit-button"
                            type="submit"
                            disabled={
                                status.loading
                            }
                        >
                            {status.loading
                                ? "Generating..."
                                : "Generate ML Recommendation"}
                        </button>

                        <button
                            className="secondary-action"
                            type="button"
                            disabled={
                                status.loading
                            }
                            onClick={
                                resetForm
                            }
                        >
                            Reset
                        </button>
                    </div>


                    {status.message && (
                        <div
                            className={
                                `form-alert ${status.type}`
                            }
                            role={
                                status.type === "error"
                                    ? "alert"
                                    : "status"
                            }
                        >
                            {status.message}
                        </div>
                    )}
                </form>


                <aside className="preview-panel">
                    <h2>
                        Recommendation Result
                    </h2>

                    {!recommendation ? (
                        <p>
                            Fill in the agricultural,
                            soil, and weather conditions
                            and generate an ML-supported
                            irrigation recommendation.
                        </p>
                    ) : (
                        <div className="recommendation-result">
                            <div className="recommendation-highlight">
                                <span>
                                    Recommendation
                                </span>

                                <strong>
                                    {recommendation.recommendation
                                        || "Recommendation generated."}
                                </strong>
                            </div>


                            <div className="preview-list">
                                <RecommendationValue
                                    label="Prediction Label"
                                    value={
                                        predictionLabel
                                    }
                                />

                                <RecommendationValue
                                    label="Source"
                                    value={
                                        recommendation.source
                                        || "ML service"
                                    }
                                />

                                <RecommendationValue
                                    label="ML Service Available"
                                    value={
                                        recommendation.mlServiceAvailable
                                        === false
                                            ? "No, fallback used"
                                            : "Yes"
                                    }
                                />
                            </div>


                            {probabilities
                                && typeof probabilities
                                === "object"
                                && (
                                    <div className="probabilities-box">
                                        <h3>
                                            Prediction Probabilities
                                        </h3>

                                        {Object.entries(
                                            probabilities,
                                        ).map(
                                            ([key, value]) => (
                                                <div
                                                    key={key}
                                                    className="probability-row"
                                                >
                                                    <span>
                                                        {key}
                                                    </span>

                                                    <strong>
                                                        {Number.isFinite(
                                                            Number(value),
                                                        )
                                                            ? Number(value)
                                                                .toFixed(3)
                                                            : "-"}
                                                    </strong>
                                                </div>
                                            ),
                                        )}
                                    </div>
                                )}


                            <details className="raw-response-details">
                                <summary>
                                    View raw backend response
                                </summary>

                                <pre>
                                    {JSON.stringify(
                                        recommendation,
                                        null,
                                        2,
                                    )}
                                </pre>
                            </details>
                        </div>
                    )}
                </aside>
            </section>
        </main>
    );
}