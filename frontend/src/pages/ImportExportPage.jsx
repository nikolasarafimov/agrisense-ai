import { useState } from "react";
import { API_BASE_URL, getAuthToken } from "../api";

const EXPORT_OPTIONS = [
    {
        key: "crops",
        label: "Export Crops CSV",
        endpoint: "/api/data/export/crops",
        filename: "crops.csv",
    },
    {
        key: "parcels",
        label: "Export Parcels CSV",
        endpoint: "/api/data/export/parcels",
        filename: "parcels.csv",
    },
    {
        key: "activities",
        label: "Export Activities CSV",
        endpoint: "/api/data/export/activities",
        filename: "activities.csv",
    },
    {
        key: "excel",
        label: "Export Complete Excel Workbook",
        endpoint: "/api/data/export/excel",
        filename: "agriculture-data.xlsx",
    },
];

const IMPORT_OPTIONS = [
    {
        key: "crops",
        label: "Import Crops CSV",
        endpoint: "/api/data/import/crops",
        accept: ".csv",
    },
    {
        key: "parcels",
        label: "Import Parcels CSV",
        endpoint: "/api/data/import/parcels",
        accept: ".csv",
    },
    {
        key: "activities",
        label: "Import Activities CSV",
        endpoint: "/api/data/import/activities",
        accept: ".csv",
    },
    {
        key: "excel",
        label: "Import Excel Workbook",
        endpoint: "/api/data/import/excel",
        accept: ".xlsx,.xls",
    },
];

export default function ImportExportPage() {

    const [status, setStatus] = useState({
        message: "",
        type: "",
    });

    const authenticatedFetch = (endpoint, options = {}) => {
        const token = getAuthToken();

        const {
            headers: customHeaders = {},
            ...requestOptions
        } = options;

        return fetch(`${API_BASE_URL}${endpoint}`, {
            ...requestOptions,
            headers: {
                ...customHeaders,
                ...(token
                    ? { Authorization: `Bearer ${token}` }
                    : {}),
            },
        });
    };

    const downloadFile = async (option) => {
        setStatus({
            message: `Preparing ${option.label.toLowerCase()}...`,
            type: "info",
        });

        try {
            const response = await authenticatedFetch(
                option.endpoint
            );

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "Export failed.");
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);

            const link = document.createElement("a");
            link.href = url;
            link.download = option.filename;
            document.body.appendChild(link);
            link.click();
            link.remove();

            window.URL.revokeObjectURL(url);

            setStatus({
                message: `${option.label} completed successfully.`,
                type: "success",
            });
        } catch (error) {
            setStatus({
                message:
                    error.message ||
                    "Export failed. Make sure the backend is running.",
                type: "error",
            });
        }
    };

    const uploadFile = async (option, file) => {
        if (!file) {
            setStatus({
                message: "Please select a file first.",
                type: "error",
            });
            return;
        }

        setStatus({
            message: `Uploading ${file.name}...`,
            type: "info",
        });

        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await authenticatedFetch(
                option.endpoint,
                {
                    method: "POST",
                    body: formData,
                }
            );

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "Import failed.");
            }

            const contentType = response.headers.get("content-type");

            let message;

            if (contentType && contentType.includes("application/json")) {
                const data = await response.json();
                message = JSON.stringify(data, null, 2);
            } else {
                message = await response.text();
            }

            setStatus({
                message: message || `${option.label} completed successfully.`,
                type: "success",
            });
        } catch (error) {
            setStatus({
                message:
                    error.message ||
                    "Import failed. Make sure the file format is correct.",
                type: "error",
            });
        }
    };

    return (
        <main className="page-shell">
            <section className="page-header-card">
                <span className="section-label">Import / Export</span>
                <h1>Data Import and Export</h1>
                <p>
                    This module supports exporting agricultural records to CSV or Excel
                    and importing datasets back into the system. All operations are
                    connected to the active user.
                </p>
            </section>

            <section className="import-export-grid">
                <article className="import-export-card">
                    <h2>Export Data</h2>
                    <p>
                        Download crops, parcels, activities, or a complete Excel workbook
                        containing project data.
                    </p>

                    <div className="action-list">
                        {EXPORT_OPTIONS.map((option) => (
                            <button
                                key={option.key}
                                type="button"
                                className="submit-button"
                                onClick={() => downloadFile(option)}
                            >
                                {option.label}
                            </button>
                        ))}
                    </div>
                </article>

                <article className="import-export-card">
                    <h2>Import Data</h2>
                    <p>
                        Upload CSV files for individual modules or an Excel workbook for
                        structured data import.
                    </p>

                    <div className="action-list">
                        {IMPORT_OPTIONS.map((option) => (
                            <label key={option.key} className="file-upload-row">
                                <span>{option.label}</span>
                                <input
                                    type="file"
                                    accept={option.accept}
                                    onChange={(event) =>
                                        uploadFile(option, event.target.files[0])
                                    }
                                />
                            </label>
                        ))}
                    </div>
                </article>
            </section>

            {status.message && (
                <div className={`form-alert ${status.type}`}>
                    <pre className="status-pre">{status.message}</pre>
                </div>
            )}
        </main>
    );
}