# Weather API Research

## Project

AgriSense AI – Intelligent System for Analysis and Recommendations in Agriculture

## Team

Team 38

---

## 1. Purpose

AgriSense AI integrates external weather data to provide current and forecast weather information relevant to agricultural monitoring and irrigation decisions.

The weather functionality is implemented in the Spring Boot backend and retrieves weather data based on geographic coordinates supplied by the client.

---

## 2. Selected Weather API

The selected weather data provider is **Open-Meteo**.

Open-Meteo provides weather forecast data through a REST API and does not require an API key for the functionality currently used by AgriSense AI.

### API Base URL

```text
https://api.open-meteo.com/v1/forecast
```

---

## 3. Reasons for Selection

Open-Meteo was selected because it provides:

- Current weather conditions
- Daily weather forecasts
- Temperature data
- Relative humidity
- Precipitation data
- Wind speed
- Geographic coordinate-based requests
- Automatic timezone handling
- JSON responses suitable for REST integration
- No API key requirement for the current project use case
- Straightforward integration with Spring Boot

---

## 4. Weather Data Used by AgriSense AI

The backend currently requests the following current weather values:

```text
temperature_2m
relative_humidity_2m
precipitation
wind_speed_10m
```

The following daily forecast values are also requested:

```text
temperature_2m_max
temperature_2m_min
precipitation_sum
```

The API request also uses:

```text
timezone=auto
```

This allows the returned weather data to use the appropriate timezone for the requested geographic location.

---

## 5. Request Parameters

AgriSense AI requires the following geographic parameters when requesting weather data:

| Parameter | Type | Valid Range | Description |
|---|---|---|---|
| `latitude` | Number | -90 to 90 | Geographic latitude |
| `longitude` | Number | -180 to 180 | Geographic longitude |

Both coordinates are required.

The backend validates the latitude and longitude before sending a request to Open-Meteo.

---

## 6. Example Request

An example request for weather information is:

```text
https://api.open-meteo.com/v1/forecast?latitude=41.9981&longitude=21.4254&current=temperature_2m,relative_humidity_2m,precipitation,wind_speed_10m&daily=temperature_2m_max,temperature_2m_min,precipitation_sum&timezone=auto
```

---

## 7. Backend Integration

The weather integration is implemented in the Spring Boot backend through the `WeatherService`.

The service performs the following steps:

1. Receives latitude and longitude.
2. Validates that both coordinates are present.
3. Validates that latitude is between -90 and 90.
4. Validates that longitude is between -180 and 180.
5. Builds the Open-Meteo request URI.
6. Sends an HTTP request to the Open-Meteo API.
7. Returns the received weather data to the client.

The backend uses Spring's `RestTemplate` to communicate with the external weather service.

Connection and read timeouts are configured so that an unavailable external service does not block backend requests indefinitely.

---

## 8. Weather Request Flow

The weather request flow is:

```text
Frontend
   |
   v
Spring Boot WeatherController
   |
   v
WeatherService
   |
   v
Open-Meteo API
   |
   v
Weather JSON Response
   |
   v
Spring Boot Backend
   |
   v
Frontend
```

The frontend does not communicate directly with Open-Meteo.

Instead, weather requests pass through the AgriSense AI backend.

---

## 9. Example Weather Data

A successful Open-Meteo response can contain data similar to:

```json
{
  "latitude": 41.996,
  "longitude": 21.423,
  "timezone": "Europe/Skopje",
  "current": {
    "temperature_2m": 24.5,
    "relative_humidity_2m": 54,
    "precipitation": 0,
    "wind_speed_10m": 8.2
  },
  "daily": {
    "temperature_2m_max": [
      27.3
    ],
    "temperature_2m_min": [
      15.4
    ],
    "precipitation_sum": [
      0.0
    ]
  }
}
```

The exact response structure and values depend on the requested location and forecast period.

---

## 10. Error Handling

The backend validates coordinates before sending the external API request.

Invalid coordinates produce a client error.

Examples of invalid input include:

```text
latitude = 100
longitude = 20
```

because latitude cannot exceed 90.

Another invalid example is:

```text
latitude = 41.99
longitude = 200
```

because longitude cannot exceed 180.

The backend also uses connection and read timeouts when communicating with Open-Meteo.

This prevents an unavailable weather provider from causing requests to remain open indefinitely.

---

## 11. Security Considerations

The current Open-Meteo integration does not require an API key.

Therefore, the project does not need to store weather API credentials in:

- Source code
- GitHub
- Environment variables
- Frontend code

This reduces the risk of exposing third-party API credentials.

Weather requests are performed by the backend rather than directly by the frontend.

---

## 12. Advantages of Open-Meteo for AgriSense AI

Open-Meteo is suitable for the current project because it provides the environmental information required by the agricultural application without introducing unnecessary authentication or credential management.

The integration provides data that can support agricultural decisions such as:

- Monitoring temperature conditions
- Monitoring humidity
- Checking rainfall and precipitation
- Monitoring wind conditions
- Supporting irrigation-related decisions
- Displaying short-term forecast information

The weather functionality complements the application's AI-based irrigation recommendation functionality.

---

## 13. Limitations

The current AgriSense AI weather integration is intentionally limited to the data required by the prototype.

The application currently does not use:

- Historical weather data
- Long-term climate analysis
- UV index
- Atmospheric pressure
- Dew point
- Weather alerts
- Radar data

These features could be added in future versions if required.

---

## 14. Conclusion

Open-Meteo is the selected weather data provider for AgriSense AI.

It provides the current and forecast weather information required by the application, supports geographic coordinate-based requests, returns structured JSON data, and does not require an API key for the current project use case.

The integration is implemented through the Spring Boot backend, which validates coordinates, communicates with Open-Meteo, and returns weather information to the frontend.

This approach provides a simple, secure, and maintainable weather integration for the AgriSense AI prototype.