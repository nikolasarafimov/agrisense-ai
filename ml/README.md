# AgriSense AI – ML Service

The `ml` module provides the machine learning inference service used by AgriSense AI to generate irrigation-need predictions.

The service is implemented with FastAPI and loads the trained preprocessing and classification artifacts when the application starts.

## Architecture

The application communicates with the ML service through the Spring Boot backend:

```text
React Frontend
      |
      v
Spring Boot Backend
      |
      v
FastAPI ML Service
      |
      v
Preprocessor + Trained Irrigation Model
```

The frontend does not communicate directly with the ML service.

## Technology

- Python 3.11
- FastAPI
- Uvicorn
- pandas
- NumPy
- scikit-learn
- XGBoost
- joblib

## Project Structure

```text
ml/
├── app/
│   ├── __init__.py
│   ├── feature_engineering.py
│   ├── main.py
│   ├── model_loader.py
│   ├── schemas.py
│   └── utils.py
├── data/
│   └── irrigation_prediction.csv
├── notebooks/
│   └── model_selection.ipynb
├── trained_model_objects/
│   ├── healthy_soil_moisture_threshold.pkl
│   ├── irrigation_model.pkl
│   ├── label_encoder.pkl
│   └── preprocessor.pkl
├── .dockerignore
├── Dockerfile
├── README.md
└── requirements.txt
```

## Model

The model predicts one of three irrigation-need classes:

- `Low`
- `Medium`
- `High`

The training workflow is documented in:

```text
notebooks/model_selection.ipynb
```

The original dataset is stored in:

```text
data/irrigation_prediction.csv
```

The trained artifacts used by the API are stored in:

```text
trained_model_objects/
```

## Feature Engineering

Before inference, four engineered features are generated from the raw request data:

```text
Moisture_Deficit
Water_Availability
ET_Proxy
Irrigation_per_Hectare
```

These features are added before the saved preprocessor is applied.

The final model input contains 23 features consisting of the original raw features and the engineered features.

## Run Locally

Install the dependencies:

```bash
pip install -r requirements.txt
```

Start the FastAPI service:

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

For development with automatic reload:

```bash
uvicorn app.main:app --reload --port 8000
```

The API will be available at:

```text
http://localhost:8000
```

FastAPI documentation is available at:

```text
http://localhost:8000/docs
```

## Docker

Build the image from the `ml` directory:

```bash
docker build -t agrisense-ml .
```

Run the container:

```bash
docker run --rm -p 8000:8000 agrisense-ml
```

When the complete AgriSense AI project is started with Docker Compose, the Spring Boot backend communicates with this service through the internal Docker network.

## Health Endpoint

### `GET /health`

Example response:

```json
{
  "status": "UP",
  "service": "agrisense-ml"
}
```

## Prediction Endpoint

### `POST /predict`

Content type:

```text
application/json
```

The endpoint accepts either:

1. one prediction object, or
2. a JSON array containing multiple prediction objects.

### Request Fields

| Field | Type |
| --- | --- |
| `Soil_pH` | `float` |
| `Soil_Moisture` | `float` |
| `Organic_Carbon` | `float` |
| `Electrical_Conductivity` | `float` |
| `Temperature_C` | `float` |
| `Humidity` | `float` |
| `Rainfall_mm` | `float` |
| `Sunlight_Hours` | `float` |
| `Wind_Speed_kmh` | `float` |
| `Field_Area_hectare` | `float` |
| `Previous_Irrigation_mm` | `float` |
| `Soil_Type` | `string` |
| `Crop_Type` | `string` |
| `Crop_Growth_Stage` | `string` |
| `Season` | `string` |
| `Irrigation_Type` | `string` |
| `Water_Source` | `string` |
| `Mulching_Used` | `string` |
| `Region` | `string` |

Numeric values must be finite.

The following values must be greater than or equal to zero:

```text
Humidity
Rainfall_mm
Sunlight_Hours
Wind_Speed_kmh
Field_Area_hectare
Previous_Irrigation_mm
```

Categorical values must be non-empty strings.

Unknown request fields are rejected.

## Training Categories

The categorical values represented in the training dataset are:

### Soil Type

```text
Clay
Loamy
Sandy
Silt
```

### Crop Type

```text
Cotton
Maize
Potato
Rice
Sugarcane
Wheat
```

### Crop Growth Stage

```text
Sowing
Vegetative
Flowering
Harvest
```

### Season

```text
Rabi
Kharif
Zaid
```

### Irrigation Type

```text
Canal
Drip
Rainfed
Sprinkler
```

### Water Source

```text
Groundwater
Rainwater
Reservoir
River
```

### Mulching Used

```text
Yes
No
```

### Region

```text
Central
East
North
South
West
```

Using values represented in the training data is recommended so that inference remains consistent with the model's training distribution.

## Example Request

```json
{
  "Soil_pH": 6.8,
  "Soil_Moisture": 32.0,
  "Organic_Carbon": 1.2,
  "Electrical_Conductivity": 0.8,
  "Temperature_C": 32.0,
  "Humidity": 35.0,
  "Rainfall_mm": 2.0,
  "Sunlight_Hours": 8.0,
  "Wind_Speed_kmh": 12.0,
  "Field_Area_hectare": 1.5,
  "Previous_Irrigation_mm": 5.0,
  "Soil_Type": "Loamy",
  "Crop_Type": "Wheat",
  "Crop_Growth_Stage": "Vegetative",
  "Season": "Kharif",
  "Irrigation_Type": "Drip",
  "Water_Source": "Groundwater",
  "Mulching_Used": "Yes",
  "Region": "Central"
}
```

## Example Response

```json
{
  "predictions": [
    {
      "label": "Medium",
      "probabilities": {
        "High": 0.05,
        "Low": 0.15,
        "Medium": 0.8
      }
    }
  ]
}
```

The exact probability values depend on the trained model and supplied input.

If the classifier does not expose probability estimates, the response contains only the prediction label.

## Batch Prediction

Multiple records can be submitted in a single request:

```json
[
  {
    "Soil_pH": 6.8,
    "Soil_Moisture": 32.0,
    "Organic_Carbon": 1.2,
    "Electrical_Conductivity": 0.8,
    "Temperature_C": 32.0,
    "Humidity": 35.0,
    "Rainfall_mm": 2.0,
    "Sunlight_Hours": 8.0,
    "Wind_Speed_kmh": 12.0,
    "Field_Area_hectare": 1.5,
    "Previous_Irrigation_mm": 5.0,
    "Soil_Type": "Loamy",
    "Crop_Type": "Wheat",
    "Crop_Growth_Stage": "Vegetative",
    "Season": "Kharif",
    "Irrigation_Type": "Drip",
    "Water_Source": "Groundwater",
    "Mulching_Used": "Yes",
    "Region": "Central"
  }
]
```

The response contains one prediction item for each submitted record.

## Validation and Error Handling

The API validates incoming data before inference.

Invalid requests return HTTP `400`.

Internal prediction failures return HTTP `500` without exposing internal exception details to the client.

ML artifacts are loaded during application startup. If a required artifact cannot be loaded, application startup fails instead of serving predictions with an incomplete model configuration.

## AgriSense AI

This service is one component of the complete AgriSense AI platform and is intended to be accessed through the Spring Boot backend.