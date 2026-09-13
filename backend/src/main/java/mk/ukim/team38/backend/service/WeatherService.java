package mk.ukim.team38.backend.service;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
public class WeatherService {

    private static final String WEATHER_API_URL =
            "https://api.open-meteo.com/v1/forecast";

    private static final int CONNECT_TIMEOUT_MS = 3000;
    private static final int READ_TIMEOUT_MS = 5000;

    private final RestTemplate restTemplate;

    public WeatherService() {
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

    public Map<String, Object> getWeatherForecast(
            Double latitude,
            Double longitude
    ) {
        validateCoordinates(
                latitude,
                longitude
        );

        URI uri = UriComponentsBuilder
                .fromUriString(WEATHER_API_URL)
                .queryParam(
                        "latitude",
                        latitude
                )
                .queryParam(
                        "longitude",
                        longitude
                )
                .queryParam(
                        "current",
                        "temperature_2m,"
                                + "relative_humidity_2m,"
                                + "precipitation,"
                                + "wind_speed_10m"
                )
                .queryParam(
                        "daily",
                        "temperature_2m_max,"
                                + "temperature_2m_min,"
                                + "precipitation_sum"
                )
                .queryParam(
                        "timezone",
                        "auto"
                )
                .build()
                .encode()
                .toUri();

        return restTemplate.getForObject(
                uri,
                Map.class
        );
    }

    private void validateCoordinates(
            Double latitude,
            Double longitude
    ) {
        if (
                latitude == null
                        || longitude == null
        ) {
            throw new IllegalArgumentException(
                    "Latitude and longitude are required."
            );
        }

        if (
                !Double.isFinite(latitude)
                        || latitude < -90
                        || latitude > 90
        ) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90."
            );
        }

        if (
                !Double.isFinite(longitude)
                        || longitude < -180
                        || longitude > 180
        ) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180."
            );
        }
    }
}