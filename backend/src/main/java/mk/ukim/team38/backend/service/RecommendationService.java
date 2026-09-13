package mk.ukim.team38.backend.service;

import mk.ukim.team38.backend.dto.RecommendationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    RecommendationService.class
            );

    private static final int CONNECT_TIMEOUT_MS = 3000;
    private static final int READ_TIMEOUT_MS = 5000;

    private final RestTemplate restTemplate;

    @Value("${ml.service.url:http://localhost:8000}")
    private String mlServiceUrl;

    public RecommendationService() {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(
                CONNECT_TIMEOUT_MS
        );

        requestFactory.setReadTimeout(
                READ_TIMEOUT_MS
        );

        this.restTemplate =
                new RestTemplate(
                        requestFactory
                );
    }

    public Map<String, Object> getRecommendation(
            RecommendationRequest request
    ) {
        Map<String, Object> mlPayload =
                buildMlPayload(request);

        try {
            Map<String, Object> mlResponse =
                    restTemplate.postForObject(
                            mlServiceUrl + "/predict",
                            mlPayload,
                            Map.class
                    );

            if (mlResponse != null) {
                return normalizeMlResponse(
                        mlResponse,
                        mlPayload
                );
            }

            return fallbackRecommendation(
                    request,
                    "ML service returned an empty response.",
                    mlPayload
            );

        } catch (RestClientException exception) {
            LOGGER.warn(
                    "ML service request failed: {}",
                    exception.getMessage()
            );

            return fallbackRecommendation(
                    request,
                    "ML service is currently unavailable.",
                    mlPayload
            );
        }
    }

    private Map<String, Object> buildMlPayload(
            RecommendationRequest request
    ) {
        Map<String, Object> mlPayload =
                new LinkedHashMap<>();

        mlPayload.put(
                "Soil_pH",
                request.getSoilPH()
        );

        mlPayload.put(
                "Soil_Moisture",
                request.getSoilMoisture()
        );

        mlPayload.put(
                "Organic_Carbon",
                request.getOrganicCarbon()
        );

        mlPayload.put(
                "Electrical_Conductivity",
                request.getElectricalConductivity()
        );

        mlPayload.put(
                "Temperature_C",
                request.getTemperatureC()
        );

        mlPayload.put(
                "Humidity",
                request.getHumidity()
        );

        mlPayload.put(
                "Rainfall_mm",
                request.getRainfallMm()
        );

        mlPayload.put(
                "Sunlight_Hours",
                request.getSunlightHours()
        );

        mlPayload.put(
                "Wind_Speed_kmh",
                request.getWindSpeedKmh()
        );

        mlPayload.put(
                "Field_Area_hectare",
                request.getFieldAreaHectare()
        );

        mlPayload.put(
                "Previous_Irrigation_mm",
                request.getPreviousIrrigationMm()
        );

        mlPayload.put(
                "Soil_Type",
                request.getSoilType()
        );

        mlPayload.put(
                "Crop_Type",
                request.getCropType()
        );

        mlPayload.put(
                "Crop_Growth_Stage",
                request.getCropGrowthStage()
        );

        mlPayload.put(
                "Season",
                request.getSeason()
        );

        mlPayload.put(
                "Irrigation_Type",
                request.getIrrigationType()
        );

        mlPayload.put(
                "Water_Source",
                request.getWaterSource()
        );

        mlPayload.put(
                "Mulching_Used",
                request.getMulchingUsed()
        );

        mlPayload.put(
                "Region",
                request.getRegion()
        );

        return mlPayload;
    }

    private Map<String, Object> normalizeMlResponse(
            Map<String, Object> mlResponse,
            Map<String, Object> mlPayload
    ) {
        Map<String, Object> response =
                new LinkedHashMap<>();

        String label =
                extractLabel(mlResponse);

        Object probabilities =
                extractProbabilities(mlResponse);

        response.put(
                "recommendation",
                buildRecommendationText(label)
        );

        response.put(
                "predictionLabel",
                label
        );

        response.put(
                "probabilities",
                probabilities
        );

        response.put(
                "source",
                "FastAPI ML service"
        );

        response.put(
                "mlServiceAvailable",
                true
        );

        response.put(
                "mlPayload",
                mlPayload
        );

        response.put(
                "rawMlResponse",
                mlResponse
        );

        return response;
    }

    private String extractLabel(
            Map<String, Object> mlResponse
    ) {
        Object predictionsObject =
                mlResponse.get("predictions");

        if (
                predictionsObject
                        instanceof List<?> predictions
                        && !predictions.isEmpty()
        ) {
            Object firstPrediction =
                    predictions.get(0);

            if (
                    firstPrediction
                            instanceof Map<?, ?> predictionMap
            ) {
                Object label =
                        predictionMap.get("label");

                if (label != null) {
                    return label.toString();
                }
            }
        }

        Object prediction =
                mlResponse.get("prediction");

        if (prediction != null) {
            return prediction.toString();
        }

        Object result =
                mlResponse.get("result");

        if (result != null) {
            return result.toString();
        }

        return "Unknown";
    }

    private Object extractProbabilities(
            Map<String, Object> mlResponse
    ) {
        Object predictionsObject =
                mlResponse.get("predictions");

        if (
                predictionsObject
                        instanceof List<?> predictions
                        && !predictions.isEmpty()
        ) {
            Object firstPrediction =
                    predictions.get(0);

            if (
                    firstPrediction
                            instanceof Map<?, ?> predictionMap
            ) {
                return predictionMap.get(
                        "probabilities"
                );
            }
        }

        return null;
    }

    private String buildRecommendationText(
            String label
    ) {
        if (label == null) {
            return "ML prediction was generated, but no clear label was returned.";
        }

        return switch (label.toLowerCase()) {
            case "high" ->
                    "High irrigation need detected. The field should be irrigated soon because the model predicts high water requirement.";

            case "medium" ->
                    "Medium irrigation need detected. The field should be monitored and moderate irrigation may be required.";

            case "low" ->
                    "Low irrigation need detected. Irrigation is currently not urgent based on the provided conditions.";

            default ->
                    "ML prediction generated: "
                            + label
                            + ".";
        };
    }

    private Map<String, Object> fallbackRecommendation(
            RecommendationRequest request,
            String reason,
            Map<String, Object> mlPayload
    ) {
        Map<String, Object> response =
                new LinkedHashMap<>();

        String recommendation;

        if (
                request.getRainfallMm() != null
                        && request.getRainfallMm() > 20
        ) {
            recommendation =
                    "Irrigation is not recommended because rainfall is high.";

        } else if (
                request.getSoilMoisture() != null
                        && request.getSoilMoisture() < 35
        ) {
            recommendation =
                    "Irrigation is recommended because soil moisture is low.";

        } else if (
                request.getTemperatureC() != null
                        && request.getTemperatureC() > 30
        ) {
            recommendation =
                    "Irrigation is recommended because temperature is high.";

        } else if (
                request.getHumidity() != null
                        && request.getHumidity() < 40
        ) {
            recommendation =
                    "Moderate irrigation is recommended because humidity is low.";

        } else {
            recommendation =
                    "Normal monitoring is recommended. No urgent irrigation action is required.";
        }

        response.put(
                "recommendation",
                recommendation
        );

        response.put(
                "predictionLabel",
                "Fallback"
        );

        response.put(
                "probabilities",
                null
        );

        response.put(
                "source",
                "Backend fallback recommendation"
        );

        response.put(
                "mlServiceAvailable",
                false
        );

        response.put(
                "reason",
                reason
        );

        response.put(
                "mlPayload",
                mlPayload
        );

        return response;
    }
}