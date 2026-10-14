# AgriSense AI – ML Service

The `ml` module provides the machine learning inference service used by AgriSense AI to generate irrigation-need predictions.

It is implemented with FastAPI and is accessed through the Spring Boot backend.

## Architecture

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
Preprocessor + Trained Model
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
- LightGBM
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

The complete training and model-selection workflow is available in:

```text
notebooks/model_selection.ipynb
```

The dataset is stored in:

```text
data/irrigation_prediction.csv
```

The trained artifacts used by the inference service are stored in:

```text
trained_model_objects/
```

## Feature Engineering

Before inference, four additional features are generated:

```text
Moisture_Deficit
Water_Availability
ET_Proxy
Irrigation_per_Hectare
```

Together with the raw request features, the model uses a final 23-feature input schema.

## Run Locally

Install dependencies:

```bash
pip install -r requirements.txt
```

Start the service:

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

For development:

```bash
uvicorn app.main:app --reload --port 8000
```

The API is available at:

```text
http://localhost:8000
```

Interactive FastAPI documentation:

```text
http://localhost:8000/docs
```

## Docker

Build the image from the `ml` directory:

```bash
docker build -t agrisense-ml .
```

Run it:

```bash
docker run --rm -p 8000:8000 agrisense-ml
```

## API

### Health Check

```http
GET /health
```

Example response:

```json
{
  "status": "UP",
  "service": "agrisense-ml"
}
```

### Prediction

```http
POST /predict
```

The endpoint accepts either a single prediction object or an array of prediction objects.

Example request:

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

Example response:

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

Exact probability values depend on the trained model and supplied input.

## Integration

The ML service is designed to run as part of the complete AgriSense AI application.

When the project is started with Docker Compose, the Spring Boot backend communicates with this service through the internal Docker network.