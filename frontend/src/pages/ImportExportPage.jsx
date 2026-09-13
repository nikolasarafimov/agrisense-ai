import { useState } from "react";

import {
    API_BASE_URL,
    getAuthToken,
} from "../api";


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
        accept: ".xlsx",
    },
];


function getErrorMessage(data) {
    if (!data) {
        return "Request failed.";
    }

    if (typeof data === "string") {
        return data;
    }

    if (data.message) {
        return data.message;
    }

    return "Request failed.";
}


async function readResponseBody(response) {
    const contentType =
        response.headers.get("content-type")
        || "";

    if (
        contentType.includes(
            "application/json",
        )
    ) {
        return response.json();
    }

    return response.text();
}


function formatImportResult(
    data,
    fallbackMessage,
) {
    if (
        !data
        || typeof data !== "object"
    ) {
        return data || fallbackMessage;
    }

    const imported =
        data.imported ?? 0;

    const skipped =
        data.skipped ?? 0;

    const errors =
        Array.isArray(data.errors)
            ? data.errors
            : [];

    let message =
        `Imported: ${imported}\nSkipped: ${skipped}`;

    if (errors.length > 0) {
        message +=
            `\n\nErrors:\n${errors
                .map(
                    (error) =>
                        `- ${error}`,
                )
                .join("\n")}`;
    }

    return message;
}


export default function ImportExportPage() {
    const [status, setStatus] =
        useState({
            message: "",
            type: "",
        });

    const [busyAction, setBusyAction] =
        useState("");


    const authenticatedFetch = (
        endpoint,
        options = {},
    ) => {
        const token =
            getAuthToken();

        const {
            headers: customHeaders = {},
            ...requestOptions
        } = options;

        return fetch(
            `${API_BASE_URL}${endpoint}`,
            {
                ...requestOptions,
                headers: {
                    ...customHeaders,
                    ...(token
                        ? {
                            Authorization:
                                `Bearer ${token}`,
                        }
                        : {}),
                },
            },
        );
    };


    const downloadFile = async (
        option,
    ) => {
        if (busyAction) {
            return;
        }

        setBusyAction(
            `export-${option.key}`,
        );

        setStatus({
            message:
                `Preparing ${option.label.toLowerCase()}...`,
            type: "info",
        });

        try {
            const response =
                await authenticatedFetch(
                    option.endpoint,
                );

            if (!response.ok) {
                const errorData =
                    await readResponseBody(
                        response,
                    );

                throw new Error(
                    getErrorMessage(
                        errorData,
                    ),
                );
            }

            const blob =
                await response.blob();

            const url =
                window.URL
                    .createObjectURL(blob);

            const link =
                document.createElement("a");

            link.href = url;
            link.download =
                option.filename;

            document.body
                .appendChild(link);

            link.click();
            link.remove();

            window.URL
                .revokeObjectURL(url);

            setStatus({
                message:
                    `${option.label} completed successfully.`,
                type: "success",
            });

        } catch (error) {
            setStatus({
                message:
                    error.message
                    || "Export failed.",
                type: "error",
            });

        } finally {
            setBusyAction("");
        }
    };


    const uploadFile = async (
        option,
        file,
    ) => {
        if (busyAction) {
            return;
        }

        if (!file) {
            setStatus({
                message:
                    "Please select a file first.",
                type: "error",
            });

            return;
        }

        const normalizedFileName =
            file.name.toLowerCase();

        if (
            option.accept === ".csv"
            && !normalizedFileName
                .endsWith(".csv")
        ) {
            setStatus({
                message:
                    "Please select a CSV file.",
                type: "error",
            });

            return;
        }

        if (
            option.accept === ".xlsx"
            && !normalizedFileName
                .endsWith(".xlsx")
        ) {
            setStatus({
                message:
                    "Please select an XLSX file.",
                type: "error",
            });

            return;
        }

        setBusyAction(
            `import-${option.key}`,
        );

        setStatus({
            message:
                `Uploading ${file.name}...`,
            type: "info",
        });

        const formData =
            new FormData();

        formData.append(
            "file",
            file,
        );

        try {
            const response =
                await authenticatedFetch(
                    option.endpoint,
                    {
                        method: "POST",
                        body: formData,
                    },
                );

            const responseData =
                await readResponseBody(
                    response,
                );

            if (!response.ok) {
                throw new Error(
                    getErrorMessage(
                        responseData,
                    ),
                );
            }

            setStatus({
                message:
                    formatImportResult(
                        responseData,
                        `${option.label} completed successfully.`,
                    ),
                type: "success",
            });

        } catch (error) {
            setStatus({
                message:
                    error.message
                    || "Import failed. Make sure the file format is correct.",
                type: "error",
            });

        } finally {
            setBusyAction("");
        }
    };


    return (
        <main className="page-shell">
            <section className="page-header-card">
                <span className="section-label">
                    AgriSense AI Import / Export
                </span>

                <h1>
                    Data Import and Export
                </h1>

                <p>
                    Export your agricultural records
                    to CSV or Excel and import
                    previously prepared datasets.
                    All operations are scoped to
                    your authenticated account.
                </p>
            </section>


            <section className="import-export-grid">
                <article className="import-export-card">
                    <h2>
                        Export Data
                    </h2>

                    <p>
                        Download your crops, parcels,
                        activities, or a complete
                        Excel workbook containing
                        all supported agricultural
                        records.
                    </p>

                    <div className="action-list">
                        {EXPORT_OPTIONS.map(
                            (option) => (
                                <button
                                    key={option.key}
                                    type="button"
                                    className="submit-button"
                                    disabled={
                                        Boolean(
                                            busyAction,
                                        )
                                    }
                                    onClick={() =>
                                        downloadFile(
                                            option,
                                        )
                                    }
                                >
                                    {busyAction
                                    === `export-${option.key}`
                                        ? "Preparing..."
                                        : option.label}
                                </button>
                            ),
                        )}
                    </div>
                </article>


                <article className="import-export-card">
                    <h2>
                        Import Data
                    </h2>

                    <p>
                        Upload CSV files for
                        individual data categories
                        or an XLSX workbook containing
                        crops, parcels, and activities.
                    </p>

                    <div className="action-list">
                        {IMPORT_OPTIONS.map(
                            (option) => (
                                <label
                                    key={option.key}
                                    className="file-upload-row"
                                >
                                    <span>
                                        {option.label}
                                    </span>

                                    <input
                                        type="file"
                                        accept={
                                            option.accept
                                        }
                                        disabled={
                                            Boolean(
                                                busyAction,
                                            )
                                        }
                                        onChange={
                                            (event) => {
                                                const file =
                                                    event
                                                        .target
                                                        .files?.[0];

                                                uploadFile(
                                                    option,
                                                    file,
                                                );

                                                event.target.value =
                                                    "";
                                            }
                                        }
                                    />
                                </label>
                            ),
                        )}
                    </div>
                </article>
            </section>


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
                    <pre className="status-pre">
                        {status.message}
                    </pre>
                </div>
            )}
        </main>
    );
}