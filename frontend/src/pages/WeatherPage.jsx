import { useState } from "react";

import {
    apiRequest,
} from "../api";


const DEFAULT_COORDINATES = {
    latitude: "41.9981",
    longitude: "21.4254",
};


export default function WeatherPage() {
    const [coordinates, setCoordinates] =
        useState(DEFAULT_COORDINATES);

    const [weatherData, setWeatherData] =
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

        setCoordinates(
            (previousCoordinates) => ({
                ...previousCoordinates,
                [name]: value,
            }),
        );
    };


    const validateCoordinates = () => {
        if (
            coordinates.latitude === ""
            || coordinates.longitude === ""
        ) {
            return "Latitude and longitude are required.";
        }

        const latitude =
            Number(coordinates.latitude);

        const longitude =
            Number(coordinates.longitude);

        if (
            !Number.isFinite(latitude)
            || latitude < -90
            || latitude > 90
        ) {
            return "Latitude must be between -90 and 90.";
        }

        if (
            !Number.isFinite(longitude)
            || longitude < -180
            || longitude > 180
        ) {
            return "Longitude must be between -180 and 180.";
        }

        return "";
    };


    const fetchWeather = async (event) => {
        event.preventDefault();

        const validationError =
            validateCoordinates();

        if (validationError) {
            setStatus({
                loading: false,
                message:
                validationError,
                type: "error",
            });

            return;
        }

        const latitude =
            Number(coordinates.latitude);

        const longitude =
            Number(coordinates.longitude);

        setStatus({
            loading: true,
            message:
                "Fetching weather data from Open-Meteo...",
            type: "info",
        });

        setWeatherData(null);

        try {
            const params =
                new URLSearchParams({
                    latitude:
                        String(latitude),
                    longitude:
                        String(longitude),
                });

            const data =
                await apiRequest(
                    `/api/weather?${params.toString()}`,
                );

            setWeatherData(data);

            setStatus({
                loading: false,
                message:
                    "Weather data loaded successfully.",
                type: "success",
            });

        } catch (error) {
            setStatus({
                loading: false,
                message:
                    error.message
                    || "Could not fetch weather data.",
                type: "error",
            });
        }
    };


    const resetCoordinates = () => {
        if (status.loading) {
            return;
        }

        setCoordinates(
            DEFAULT_COORDINATES,
        );

        setWeatherData(null);

        setStatus({
            loading: false,
            message: "",
            type: "",
        });
    };


    const current =
        weatherData?.current;

    const currentUnits =
        weatherData?.current_units;


    return (
        <main className="page-shell">
            <section className="page-header-card">
                <span className="section-label">
                    AgriSense AI Weather Integration
                </span>

                <h1>
                    Weather Data
                </h1>

                <p>
                    Retrieve current weather conditions
                    and forecast data for geographic
                    coordinates through the backend
                    Open-Meteo integration.
                </p>
            </section>


            <section className="form-layout">
                <form
                    className="data-form"
                    onSubmit={fetchWeather}
                >
                    <div className="form-field">
                        <label htmlFor="latitude">
                            Latitude
                        </label>

                        <input
                            id="latitude"
                            name="latitude"
                            type="number"
                            min="-90"
                            max="90"
                            step="any"
                            value={
                                coordinates.latitude
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
                        <label htmlFor="longitude">
                            Longitude
                        </label>

                        <input
                            id="longitude"
                            name="longitude"
                            type="number"
                            min="-180"
                            max="180"
                            step="any"
                            value={
                                coordinates.longitude
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


                    <div className="form-actions-row">
                        <button
                            className="submit-button"
                            type="submit"
                            disabled={
                                status.loading
                            }
                        >
                            {status.loading
                                ? "Loading Weather..."
                                : "Get Weather Data"}
                        </button>

                        <button
                            className="secondary-action"
                            type="button"
                            disabled={
                                status.loading
                            }
                            onClick={
                                resetCoordinates
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
                        Current Weather
                    </h2>

                    {!current ? (
                        <p>
                            Enter geographic coordinates
                            and request weather data.
                            The default coordinates point
                            to Skopje.
                        </p>
                    ) : (
                        <div className="preview-list">
                            <div>
                                <span>
                                    Temperature
                                </span>

                                <strong>
                                    {current.temperature_2m}{" "}
                                    {currentUnits?.temperature_2m
                                        || "°C"}
                                </strong>
                            </div>

                            <div>
                                <span>
                                    Humidity
                                </span>

                                <strong>
                                    {current.relative_humidity_2m}{" "}
                                    {currentUnits?.relative_humidity_2m
                                        || "%"}
                                </strong>
                            </div>

                            <div>
                                <span>
                                    Precipitation
                                </span>

                                <strong>
                                    {current.precipitation}{" "}
                                    {currentUnits?.precipitation
                                        || "mm"}
                                </strong>
                            </div>

                            <div>
                                <span>
                                    Wind Speed
                                </span>

                                <strong>
                                    {current.wind_speed_10m}{" "}
                                    {currentUnits?.wind_speed_10m
                                        || "km/h"}
                                </strong>
                            </div>
                        </div>
                    )}
                </aside>
            </section>
        </main>
    );
}