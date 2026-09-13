# AgriSense AI

**AgriSense AI** is a full-stack academic prototype for agricultural data management and AI-assisted irrigation recommendations.

The platform allows authenticated users to manage crops, parcels, and agricultural activities, view personalized dashboard statistics, import and export agricultural data, access weather information, and generate machine-learning-supported irrigation recommendations.

The system combines a React frontend, Spring Boot REST API, PostgreSQL database, FastAPI machine learning service, and Docker Compose.

> Developed by Team 38 for the **ICT Project Management** course.

---

## Overview

AgriSense AI demonstrates how modern web technologies, external data sources, and machine learning can be combined into a single agricultural decision-support platform.

The application provides:

- secure user registration and authentication
- JWT-based authorization
- user-owned agricultural records
- crop, parcel, and activity management
- personalized dashboard statistics
- search and filtering
- weather data integration
- CSV and Excel import/export
- AI-assisted irrigation recommendations
- role-based administration
- Dockerized multi-service deployment

The project is designed as a working academic prototype with a clear separation between frontend, backend, database, and machine learning components.

---

## Main Features

### Authentication and Security

- User registration and login
- JWT-based authentication
- Password hashing
- Protected backend endpoints
- User-owned agricultural data
- Role-based authorization
- Backend-enforced `ADMIN` access
- Protected frontend routes
- Configurable CORS policy
- Structured API error responses
- Request validation

### Agricultural Data Management

Users can create, view, search, edit, and delete:

- crops
- parcels
- agricultural activities

Each record belongs to the authenticated user. User identity is derived from the JWT token and is never supplied through a `userId` request parameter.

### Dashboard

The dashboard provides statistics for the currently authenticated user:

- number of crops
- number of parcels
- number of activities
- total agricultural records

It also provides search and record management functionality.

### Import and Export

Supported formats:

- CSV
- Microsoft Excel (`.xlsx`)

Supported data:

- crops
- parcels
- activities

Imported data automatically belongs to the authenticated user.

### Weather Integration

The backend exposes a weather endpoint that retrieves weather information for selected coordinates.

Weather information can include:

- temperature
- humidity
- precipitation
- wind speed

### AI Recommendations

The Spring Boot backend communicates with a separate FastAPI service that hosts the machine learning model.

The ML component uses agricultural, soil, weather, and field-related input to generate irrigation-related predictions and recommendations.

### Administration

Users with the `ADMIN` role can access administrative functionality for:

- users
- crops
- parcels
- activities

Admin authorization is enforced by Spring Security on the backend as well as by protected frontend routing.

---

## System Architecture

```mermaid
flowchart LR
    U[User] --> F[React + Vite Frontend]

    F -->|REST / JSON + JWT| B[Spring Boot Backend]

    B -->|JPA| DB[(PostgreSQL)]
    B -->|HTTP| ML[FastAPI ML Service]
    B -->|HTTP| W[External Weather API]

    ML --> M[Trained ML Model]
```

The system consists of four primary application components:

| Component | Technology | Purpose |
|---|---|---|
| Frontend | React + Vite | User interface |
| Backend | Spring Boot | REST API, security and business logic |
| Database | PostgreSQL | Persistent application data |
| ML Service | FastAPI | Irrigation prediction and recommendations |

Docker Compose is used to run the complete system as a multi-container application.

---

## Technology Stack

### Frontend

- React
- Vite
- React Router
- JavaScript
- CSS
- Nginx

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- JJWT
- PostgreSQL Driver
- Apache POI
- OpenCSV
- Springdoc OpenAPI
- Gradle

### Database

- PostgreSQL
- H2 for automated backend tests

### Machine Learning

- Python
- FastAPI
- pandas
- scikit-learn
- joblib
- Uvicorn

### DevOps and Tooling

- Git
- GitHub
- Docker
- Docker Compose
- Jira
- Gradle
- npm

---

## Project Structure

```text
agrisense-ai/
├── backend/
│   ├── src/main/java/
│   │   └── mk/ukim/team38/backend/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── exception/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── security/
│   │       └── service/
│   └── src/test/
│
├── frontend/
│   ├── src/
│   ├── public/
│   └── Dockerfile
│
├── ml/
│   ├── app/
│   ├── data/
│   └── requirements.txt
│
├── docs/
│   └── import-export-specification.md
│
├── .env.example
├── .gitignore
├── docker-compose.yml
└── README.md
```

---

## Security Model

AgriSense AI uses stateless JWT authentication.

After successful registration or login, the backend returns a JWT token. The frontend sends the token with protected API requests:

```http
Authorization: Bearer <token>
```

Spring Security validates the token before allowing access to protected resources.

### Public endpoints

The application exposes selected endpoints without authentication, including:

```http
POST /api/users/register
POST /api/users/login
GET  /api/weather
GET  /api/health
```

### Authenticated endpoints

Agricultural records, dashboard data, profile management, import/export, and recommendation functionality require authentication.

### Administrator endpoints

Routes under:

```text
/api/admin/**
```

require the authenticated user to have the `ADMIN` role.

### Data Ownership

Crop, parcel, and activity operations are scoped to the authenticated user.

For example:

```http
GET /api/crops
```

returns only crops belonging to the current user.

A client cannot access another user's records by supplying a different user ID.

---

## REST API Overview

### Authentication and Profile

```http
POST /api/users/register
POST /api/users/login

GET  /api/users/me
PUT  /api/users/me
```

Logout is handled on the frontend by removing the locally stored authentication state.

---

### Crops

```http
GET    /api/crops
GET    /api/crops?search=tomato
GET    /api/crops/{id}

POST   /api/crops
PUT    /api/crops/{id}
DELETE /api/crops/{id}
```

Example request:

```json
{
  "name": "Tomato",
  "type": "Vegetable",
  "plantingDate": "2026-05-15"
}
```

---

### Parcels

```http
GET    /api/parcels
GET    /api/parcels?search=loamy
GET    /api/parcels/{id}

POST   /api/parcels
PUT    /api/parcels/{id}
DELETE /api/parcels/{id}
```

Example request:

```json
{
  "location": "Skopje",
  "size": 1200,
  "soilType": "Loamy"
}
```

---

### Activities

```http
GET    /api/activities
GET    /api/activities?search=irrigation
GET    /api/activities/{id}

POST   /api/activities
PUT    /api/activities/{id}
DELETE /api/activities/{id}
```

Example request:

```json
{
  "description": "Morning irrigation",
  "date": "2026-05-15",
  "type": "Irrigation"
}
```

Dates are represented using ISO format:

```text
yyyy-MM-dd
```

---

### Dashboard

```http
GET /api/dashboard/stats
```

Example response:

```json
{
  "cropsCount": 5,
  "parcelsCount": 3,
  "activitiesCount": 8,
  "totalRecords": 16
}
```

Statistics are calculated only for the authenticated user.

---

### Weather

```http
GET /api/weather?latitude=41.9981&longitude=21.4254
```

Example coordinates:

```text
Latitude:  41.9981
Longitude: 21.4254
```

---

### Recommendations

```http
POST /api/recommendations
```

The backend forwards validated recommendation input to the FastAPI ML service.

---

### Import and Export

```http
GET  /api/data/export/crops
POST /api/data/import/crops

GET  /api/data/export/parcels
POST /api/data/import/parcels

GET  /api/data/export/activities
POST /api/data/import/activities

GET  /api/data/export/excel
POST /api/data/import/excel
```

No `userId` parameter is required. The authenticated user is determined from the JWT token.

---

### Administration

```http
GET    /api/admin/users
GET    /api/admin/crops
GET    /api/admin/parcels
GET    /api/admin/activities

DELETE /api/admin/users/{id}
DELETE /api/admin/crops/{id}
DELETE /api/admin/parcels/{id}
DELETE /api/admin/activities/{id}
```

These endpoints require the `ADMIN` role.

---

## API Error Format

Backend errors use a consistent JSON structure.

Example validation error:

```json
{
  "timestamp": "2026-09-13T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed.",
  "path": "/api/users/register",
  "validationErrors": {
    "email": "Email must be valid.",
    "password": "Password must contain between 6 and 72 characters."
  }
}
```

The API uses appropriate HTTP status codes including:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
500 Internal Server Error
```

---

## Import and Export Formats

### Crops CSV

```csv
name,type,plantingDate
Tomato,Vegetable,2026-05-15
Wheat,Cereal,2026-04-20
Apple,Fruit,2026-03-10
```

### Parcels CSV

```csv
location,size,soilType
Skopje,1200,Loamy
Bitola,850,Clay
Ohrid,600,Sandy
```

### Activities CSV

```csv
description,date,type
Morning irrigation,2026-05-15,Irrigation
Soil fertilization,2026-05-16,Fertilization
Pest inspection,2026-05-17,Crop Protection
```

Excel export produces:

```text
agriculture-data.xlsx
```

with separate sheets:

```text
Crops
Parcels
Activities
```

Both CSV and Excel imports validate date fields and use ISO dates where textual dates are provided.

---

## Machine Learning Service

The ML component runs independently as a FastAPI service.

Main endpoints:

```http
GET  /health
POST /predict
```

Local service URL:

```text
http://localhost:8000
```

FastAPI documentation:

```text
http://localhost:8000/docs
```

### Recommendation Request Example

```json
{
  "Soil_pH": 6.8,
  "Soil_Moisture": 32,
  "Organic_Carbon": 1.7,
  "Electrical_Conductivity": 0.8,
  "Temperature_C": 32,
  "Humidity": 35,
  "Rainfall_mm": 2,
  "Sunlight_Hours": 8,
  "Wind_Speed_kmh": 12,
  "Field_Area_hectare": 1.5,
  "Previous_Irrigation_mm": 5,
  "Soil_Type": "Loamy",
  "Crop_Type": "Tomato",
  "Crop_Growth_Stage": "Vegetative",
  "Season": "Summer",
  "Irrigation_Type": "Drip",
  "Water_Source": "Canal",
  "Mulching_Used": "Yes",
  "Region": "Skopje"
}
```

The exact prediction result depends on the trained model.

---

# Getting Started

## Prerequisites

For Docker-based startup:

- Docker
- Docker Compose

For manual development:

- Java 21
- Node.js
- npm
- Python 3
- PostgreSQL

---

## Environment Configuration

Copy the example configuration:

### Windows PowerShell

```powershell
Copy-Item .env.example .env
```

### Linux / macOS

```bash
cp .env.example .env
```

The project uses the following environment variables:

```env
POSTGRES_DB=agriculture_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=change_me

SPRING_JPA_HIBERNATE_DDL_AUTO=update

ML_SERVICE_URL=http://ml-service:8000

VITE_API_BASE_URL=http://localhost:8080

JWT_SECRET=REPLACE_WITH_32_BYTE_BASE64_SECRET
JWT_EXPIRATION_MS=86400000

CORS_ALLOWED_ORIGINS=http://localhost:5173
```

Before starting the application, replace:

```text
POSTGRES_PASSWORD
JWT_SECRET
```

with your own values.

Do not commit `.env`.

---

## Generate a JWT Secret

The JWT secret should be a sufficiently long Base64-encoded random value.

One example using OpenSSL:

```bash
openssl rand -base64 32
```

Copy the generated value into:

```env
JWT_SECRET=<generated-value>
```

The real secret must remain only in the local `.env` file or deployment secret management system.

---

# Running with Docker Compose

Docker Compose is the recommended way to start the complete application.

From the repository root:

```bash
docker compose up --build
```

This starts:

```text
PostgreSQL
Spring Boot backend
FastAPI ML service
React/Nginx frontend
```

After startup:

| Service | URL |
|---|---|
| Frontend | http://localhost:5173 |
| Backend | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| ML Service | http://localhost:8000 |
| ML Documentation | http://localhost:8000/docs |

Stop the application:

```bash
docker compose down
```

Stop the application and delete the PostgreSQL volume:

```bash
docker compose down -v
```

> `docker compose down -v` permanently removes the local Docker database volume and should only be used when a clean database reset is intended.

---

# Manual Development

## Backend

Ensure PostgreSQL is running and configure the required environment variables for the local database.

### Windows

```powershell
cd backend
.\gradlew.bat bootRun
```

### Linux / macOS

```bash
cd backend
./gradlew bootRun
```

Backend:

```text
http://localhost:8080
```

---

## Frontend

```bash
cd frontend
npm ci
npm run dev
```

Frontend:

```text
http://localhost:5173
```

The frontend communicates with the backend using:

```env
VITE_API_BASE_URL=http://localhost:8080
```

---

## Machine Learning Service

### Windows

```powershell
cd ml
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

### Linux / macOS

```bash
cd ml
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

ML service:

```text
http://localhost:8000
```

---

# Testing

## Backend Tests

### Windows

```powershell
cd backend
.\gradlew.bat clean test
```

### Linux / macOS

```bash
cd backend
./gradlew clean test
```

The backend test suite includes coverage for areas such as:

- application context startup
- import/export endpoints
- CSV date parsing
- CSV date validation
- authentication
- authorization
- invalid credentials
- protected endpoints
- validation errors
- duplicate users
- missing resources
- case-insensitive email handling

---

## Frontend Validation

```bash
cd frontend
npm run lint
npm run build
```

These commands verify frontend code quality and confirm that a production build can be generated successfully.

---

# Frontend Pages

The React application contains the main pages required to demonstrate the platform:

```text
Home
Login
Register
Dashboard
Data Entry
Profile
Recommendations
Import / Export
Weather
Admin
```

Protected routes prevent unauthenticated users from opening secured application pages.

The Admin page additionally requires the `ADMIN` role.

---

# Data Validation

Backend request DTOs use Bean Validation.

Examples include:

- required names and descriptions
- email format validation
- password length validation
- maximum text lengths
- positive parcel sizes
- required dates
- `LocalDate` parsing
- structured malformed-request handling

Invalid request bodies return HTTP `400 Bad Request`.

---

# Demonstration Flow

A recommended demonstration sequence is:

1. Open the AgriSense AI home page.
2. Register a new account.
3. Log in and demonstrate JWT-protected navigation.
4. Add a crop, parcel, and agricultural activity.
5. Open the dashboard and view personalized statistics.
6. Search agricultural records.
7. Demonstrate edit and delete operations.
8. Open the Weather page.
9. Generate an AI-assisted recommendation.
10. Export user data as CSV or Excel.
11. Import agricultural data.
12. Open the profile page.
13. Optionally demonstrate `ADMIN` functionality.

For a shorter demonstration:

```text
Authentication
Dashboard
Data Entry
Weather
AI Recommendations
Import / Export
```

---

# Project Context

The project was developed incrementally through multiple phases, including:

- team organization and topic selection
- requirements and specification preparation
- Jira and GitHub setup
- backend and frontend initialization
- relational database development
- user authentication
- agricultural CRUD functionality
- dashboard implementation
- search and filtering
- machine learning research and model integration
- weather integration
- CSV/Excel import and export
- Docker integration
- authentication and authorization hardening
- automated testing
- final integration and documentation

---

# Current Status

AgriSense AI is a working academic prototype with the major application components fully integrated.

Implemented functionality includes:

```text
React frontend
Spring Boot REST API
PostgreSQL persistence
FastAPI ML service
JWT authentication
Spring Security authorization
User-owned agricultural data
ADMIN role enforcement
Crop management
Parcel management
Activity management
Profile management
Personalized dashboard statistics
Search and filtering
Weather integration
AI-assisted recommendations
CSV import/export
Excel import/export
Request validation
Structured API error handling
Automated backend tests
Docker Compose environment
```

---

# Future Improvements

Potential future development includes:

- refresh-token authentication
- account email verification
- password reset workflow
- database migrations with Flyway or Liquibase
- production-grade secret management
- expanded integration and end-to-end tests
- CI/CD pipeline
- cloud deployment
- advanced dashboard analytics
- expanded machine learning evaluation
- additional agricultural recommendation models
- deeper integration between live weather data and model predictions
- centralized production logging and monitoring
- further accessibility and responsive UI improvements

---

# Team

**Team 38 — ICT Project Management**

1. Nikola Sarafimov
2. Sofija Andonova
3. Atanas Vitanov
4. Marko Trajkovski
5. Kire Boškovski
6. Ivan Perchuklieski
7. Mila Todorovska
8. Barbara Popovska
9. Nikola Popov
10. Zagorka Anevska

---

## Project Theme

**Artificial Intelligence in Agriculture**

**Selected topic:**  
Intelligent System for Analysis and Recommendations in Agriculture

---

## Status

```text
Project: AgriSense AI
Type: Full-stack academic prototype
Course: ICT Project Management
Team: 38
Status: Working prototype
```