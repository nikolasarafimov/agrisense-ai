import { useEffect, useState } from "react";

import {
    apiRequest,
    getCurrentUser,
} from "../api";

import "./DashboardPage.css";


function StatCard({
                      title,
                      value,
                      description,
                  }) {
    return (
        <article className="dashboard-stat-card">
            <p>{title}</p>
            <strong>{value}</strong>
            <span>{description}</span>
        </article>
    );
}


function DataTable({
                       title,
                       columns,
                       rows,
                       emptyMessage,
                       onEdit,
                       onDelete,
                   }) {
    return (
        <section className="dashboard-table-card">
            <h3>{title}</h3>

            {rows.length === 0 ? (
                <p className="empty-table-message">
                    {emptyMessage}
                </p>
            ) : (
                <div className="responsive-table">
                    <table>
                        <thead>
                        <tr>
                            {columns.map((column) => (
                                <th
                                    key={column.key}
                                    scope="col"
                                >
                                    {column.label}
                                </th>
                            ))}

                            <th scope="col">
                                Actions
                            </th>
                        </tr>
                        </thead>

                        <tbody>
                        {rows.map((row) => (
                            <tr key={row.id}>
                                {columns.map((column) => (
                                    <td key={column.key}>
                                        {column.render
                                            ? column.render(row)
                                            : row[column.key] ?? "-"}
                                    </td>
                                ))}

                                <td>
                                    <div className="table-actions">
                                        <button
                                            type="button"
                                            className="edit-row-button"
                                            onClick={() =>
                                                onEdit(row)
                                            }
                                        >
                                            Edit
                                        </button>

                                        <button
                                            type="button"
                                            className="delete-row-button"
                                            onClick={() =>
                                                onDelete(row.id)
                                            }
                                        >
                                            Delete
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </section>
    );
}


function EditDataForm({
                          editType,
                          editForm,
                          setEditForm,
                          onCancel,
                          onSave,
                          loading,
                      }) {
    if (!editType || !editForm) {
        return null;
    }

    const handleChange = (event) => {
        const {
            name,
            value,
        } = event.target;

        setEditForm((previousForm) => ({
            ...previousForm,
            [name]: value,
        }));
    };

    const title =
        editType === "crop"
            ? "Crop"
            : editType === "parcel"
                ? "Parcel"
                : "Activity";

    return (
        <section className="edit-data-card">
            <div className="edit-data-header">
                <div>
                    <span className="section-label">
                        Edit Data
                    </span>

                    <h2>
                        Edit {title}
                    </h2>
                </div>

                <button
                    type="button"
                    className="secondary-dashboard-button"
                    disabled={loading}
                    onClick={onCancel}
                >
                    Cancel
                </button>
            </div>

            <form
                className="edit-data-form"
                onSubmit={onSave}
            >
                {editType === "crop" && (
                    <>
                        <div className="form-field">
                            <label htmlFor="edit-crop-name">
                                Crop Name
                            </label>

                            <input
                                id="edit-crop-name"
                                name="name"
                                type="text"
                                maxLength={100}
                                value={editForm.name ?? ""}
                                disabled={loading}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-field">
                            <label htmlFor="edit-crop-type">
                                Crop Type
                            </label>

                            <input
                                id="edit-crop-type"
                                name="type"
                                type="text"
                                maxLength={100}
                                value={editForm.type ?? ""}
                                disabled={loading}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-field">
                            <label htmlFor="edit-crop-date">
                                Planting Date
                            </label>

                            <input
                                id="edit-crop-date"
                                name="plantingDate"
                                type="date"
                                value={
                                    editForm.plantingDate
                                    ?? ""
                                }
                                disabled={loading}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </>
                )}

                {editType === "parcel" && (
                    <>
                        <div className="form-field">
                            <label htmlFor="edit-parcel-location">
                                Location
                            </label>

                            <input
                                id="edit-parcel-location"
                                name="location"
                                type="text"
                                maxLength={200}
                                value={
                                    editForm.location
                                    ?? ""
                                }
                                disabled={loading}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-field">
                            <label htmlFor="edit-parcel-size">
                                Size
                            </label>

                            <input
                                id="edit-parcel-size"
                                name="size"
                                type="number"
                                min="0"
                                step="any"
                                value={
                                    editForm.size
                                    ?? ""
                                }
                                disabled={loading}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-field">
                            <label htmlFor="edit-parcel-soil">
                                Soil Type
                            </label>

                            <input
                                id="edit-parcel-soil"
                                name="soilType"
                                type="text"
                                maxLength={100}
                                value={
                                    editForm.soilType
                                    ?? ""
                                }
                                disabled={loading}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </>
                )}

                {editType === "activity" && (
                    <>
                        <div className="form-field">
                            <label htmlFor="edit-activity-description">
                                Description
                            </label>

                            <input
                                id="edit-activity-description"
                                name="description"
                                type="text"
                                maxLength={255}
                                value={
                                    editForm.description
                                    ?? ""
                                }
                                disabled={loading}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-field">
                            <label htmlFor="edit-activity-type">
                                Activity Type
                            </label>

                            <input
                                id="edit-activity-type"
                                name="type"
                                type="text"
                                maxLength={100}
                                value={
                                    editForm.type
                                    ?? ""
                                }
                                disabled={loading}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-field">
                            <label htmlFor="edit-activity-date">
                                Date
                            </label>

                            <input
                                id="edit-activity-date"
                                name="date"
                                type="date"
                                value={
                                    editForm.date
                                    ?? ""
                                }
                                disabled={loading}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </>
                )}

                <button
                    type="submit"
                    className="submit-button"
                    disabled={loading}
                >
                    {loading
                        ? "Saving..."
                        : "Save Changes"}
                </button>
            </form>
        </section>
    );
}


async function fetchDashboardData(
    searchValue = "",
) {
    const normalizedSearch =
        searchValue.trim();

    const query =
        normalizedSearch
            ? `?search=${encodeURIComponent(
                normalizedSearch,
            )}`
            : "";

    const [
        stats,
        crops,
        parcels,
        activities,
    ] = await Promise.all([
        apiRequest("/api/dashboard/stats"),
        apiRequest(`/api/crops${query}`),
        apiRequest(`/api/parcels${query}`),
        apiRequest(`/api/activities${query}`),
    ]);

    return {
        stats,
        crops,
        parcels,
        activities,
    };
}


export default function DashboardPage() {
    const [currentUser] =
        useState(() => getCurrentUser());

    const [stats, setStats] =
        useState({
            cropsCount: 0,
            parcelsCount: 0,
            activitiesCount: 0,
            totalRecords: 0,
        });

    const [search, setSearch] =
        useState("");

    const [crops, setCrops] =
        useState([]);

    const [parcels, setParcels] =
        useState([]);

    const [activities, setActivities] =
        useState([]);

    const [editType, setEditType] =
        useState("");

    const [editForm, setEditForm] =
        useState(null);

    const [status, setStatus] =
        useState(() => ({
            loading: Boolean(currentUser),
            message: currentUser
                ? "Loading dashboard data..."
                : "No logged-in user found.",
            type: currentUser
                ? "info"
                : "error",
        }));


    useEffect(() => {
        if (!currentUser) {
            return;
        }

        let cancelled = false;

        const loadInitialDashboard =
            async () => {
                try {
                    const data =
                        await fetchDashboardData();

                    if (cancelled) {
                        return;
                    }

                    setStats(data.stats);
                    setCrops(data.crops);
                    setParcels(data.parcels);
                    setActivities(data.activities);

                    setStatus({
                        loading: false,
                        message: "",
                        type: "",
                    });
                } catch (error) {
                    if (cancelled) {
                        return;
                    }

                    setStatus({
                        loading: false,
                        message:
                            error.message
                            || "Could not load dashboard data.",
                        type: "error",
                    });
                }
            };

        loadInitialDashboard();

        return () => {
            cancelled = true;
        };
    }, [currentUser]);


    const refreshDashboard =
        async (searchValue = "") => {
            const data =
                await fetchDashboardData(
                    searchValue,
                );

            setStats(data.stats);
            setCrops(data.crops);
            setParcels(data.parcels);
            setActivities(data.activities);
        };


    const handleSearchSubmit =
        async (event) => {
            event.preventDefault();

            setStatus({
                loading: true,
                message:
                    "Searching agricultural data...",
                type: "info",
            });

            try {
                await refreshDashboard(search);

                setStatus({
                    loading: false,
                    message: "",
                    type: "",
                });
            } catch (error) {
                setStatus({
                    loading: false,
                    message:
                        error.message
                        || "Could not search dashboard data.",
                    type: "error",
                });
            }
        };


    const clearSearch = async () => {
        setSearch("");

        setStatus({
            loading: true,
            message:
                "Loading dashboard data...",
            type: "info",
        });

        try {
            await refreshDashboard("");

            setStatus({
                loading: false,
                message: "",
                type: "",
            });
        } catch (error) {
            setStatus({
                loading: false,
                message:
                    error.message
                    || "Could not load dashboard data.",
                type: "error",
            });
        }
    };


    const startEdit = (
        type,
        row,
    ) => {
        setEditType(type);

        setEditForm({
            ...row,
        });

        setStatus({
            loading: false,
            message:
                `Editing ${type} record with ID ${row.id}.`,
            type: "info",
        });

        window.scrollTo({
            top: 0,
            behavior: "smooth",
        });
    };


    const cancelEdit = () => {
        setEditType("");
        setEditForm(null);

        setStatus({
            loading: false,
            message: "Edit cancelled.",
            type: "info",
        });
    };


    const saveEdit = async (event) => {
        event.preventDefault();

        if (!editType || !editForm?.id) {
            setStatus({
                loading: false,
                message:
                    "No record selected for editing.",
                type: "error",
            });

            return;
        }

        let endpoint;
        let payload;

        if (editType === "crop") {
            const name =
                editForm.name?.trim();

            const type =
                editForm.type?.trim();

            if (
                !name
                || !type
                || !editForm.plantingDate
            ) {
                setStatus({
                    loading: false,
                    message:
                        "Please fill in all crop fields.",
                    type: "error",
                });

                return;
            }

            endpoint =
                `/api/crops/${editForm.id}`;

            payload = {
                name,
                type,
                plantingDate:
                editForm.plantingDate,
            };
        }

        if (editType === "parcel") {
            const location =
                editForm.location?.trim();

            const soilType =
                editForm.soilType?.trim();

            const size =
                Number(editForm.size);

            if (
                !location
                || !soilType
                || !Number.isFinite(size)
                || size <= 0
            ) {
                setStatus({
                    loading: false,
                    message:
                        "Please enter valid parcel data.",
                    type: "error",
                });

                return;
            }

            endpoint =
                `/api/parcels/${editForm.id}`;

            payload = {
                location,
                size,
                soilType,
            };
        }

        if (editType === "activity") {
            const description =
                editForm.description?.trim();

            const type =
                editForm.type?.trim();

            if (
                !description
                || !type
                || !editForm.date
            ) {
                setStatus({
                    loading: false,
                    message:
                        "Please fill in all activity fields.",
                    type: "error",
                });

                return;
            }

            endpoint =
                `/api/activities/${editForm.id}`;

            payload = {
                description,
                date: editForm.date,
                type,
            };
        }

        if (!endpoint || !payload) {
            setStatus({
                loading: false,
                message:
                    "Unsupported record type.",
                type: "error",
            });

            return;
        }

        setStatus({
            loading: true,
            message: "Saving changes...",
            type: "info",
        });

        try {
            await apiRequest(
                endpoint,
                {
                    method: "PUT",
                    body:
                        JSON.stringify(payload),
                },
            );

            setEditType("");
            setEditForm(null);

            await refreshDashboard(search);

            setStatus({
                loading: false,
                message:
                    "Record updated successfully.",
                type: "success",
            });
        } catch (error) {
            setStatus({
                loading: false,
                message:
                    error.message
                    || "Could not update record.",
                type: "error",
            });
        }
    };


    const deleteRecord = async (
        type,
        id,
    ) => {
        const confirmed =
            window.confirm(
                `Are you sure you want to delete this ${type} record?`,
            );

        if (!confirmed) {
            return;
        }

        const resource =
            type === "crop"
                ? "crops"
                : type === "parcel"
                    ? "parcels"
                    : type === "activity"
                        ? "activities"
                        : null;

        if (!resource) {
            setStatus({
                loading: false,
                message:
                    "Unsupported record type.",
                type: "error",
            });

            return;
        }

        setStatus({
            loading: true,
            message: "Deleting record...",
            type: "info",
        });

        try {
            await apiRequest(
                `/api/${resource}/${id}`,
                {
                    method: "DELETE",
                },
            );

            if (
                editType === type
                && editForm?.id === id
            ) {
                setEditType("");
                setEditForm(null);
            }

            await refreshDashboard(search);

            setStatus({
                loading: false,
                message:
                    "Record deleted successfully.",
                type: "success",
            });
        } catch (error) {
            setStatus({
                loading: false,
                message:
                    error.message
                    || "Could not delete record.",
                type: "error",
            });
        }
    };


    return (
        <main className="dashboard-page">
            <section className="dashboard-hero">
                <span className="section-label">
                    AgriSense AI Dashboard
                </span>

                <h1>
                    Agricultural Data Overview
                </h1>

                <p>
                    View your agricultural statistics,
                    search and manage records, and keep
                    track of crops, parcels, and field
                    activities from one dashboard.
                </p>

                {currentUser && (
                    <div className="current-user-box">
                        Logged in as{" "}
                        <strong>
                            {currentUser.fullName}
                        </strong>
                        {" · "}
                        {currentUser.email}
                        {" · "}
                        Role: {currentUser.role}
                    </div>
                )}
            </section>


            <section className="dashboard-stats-grid">
                <StatCard
                    title="Total Records"
                    value={stats.totalRecords}
                    description="Total agricultural records in your account"
                />

                <StatCard
                    title="Crops"
                    value={stats.cropsCount}
                    description="Saved agricultural crop records"
                />

                <StatCard
                    title="Parcels"
                    value={stats.parcelsCount}
                    description="Registered land parcel records"
                />

                <StatCard
                    title="Activities"
                    value={stats.activitiesCount}
                    description="Agricultural activity records"
                />
            </section>


            <section className="dashboard-search-card">
                <form onSubmit={handleSearchSubmit}>
                    <label htmlFor="dashboard-search">
                        Search and filter agricultural data
                    </label>

                    <div className="dashboard-search-row">
                        <input
                            id="dashboard-search"
                            type="search"
                            placeholder="Search by crop, type, location, soil, activity..."
                            value={search}
                            disabled={status.loading}
                            onChange={(event) =>
                                setSearch(
                                    event.target.value,
                                )
                            }
                        />

                        <button
                            type="submit"
                            disabled={status.loading}
                        >
                            Search
                        </button>

                        <button
                            type="button"
                            className="secondary-dashboard-button"
                            disabled={status.loading}
                            onClick={clearSearch}
                        >
                            Clear
                        </button>
                    </div>
                </form>
            </section>


            {status.message && (
                <div
                    className={
                        `dashboard-message ${status.type}`
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


            <EditDataForm
                editType={editType}
                editForm={editForm}
                setEditForm={setEditForm}
                onCancel={cancelEdit}
                onSave={saveEdit}
                loading={status.loading}
            />


            <section className="dashboard-data-grid">
                <DataTable
                    title="Crops"
                    rows={crops}
                    emptyMessage="No crops found."
                    onEdit={(row) =>
                        startEdit(
                            "crop",
                            row,
                        )
                    }
                    onDelete={(id) =>
                        deleteRecord(
                            "crop",
                            id,
                        )
                    }
                    columns={[
                        {
                            key: "id",
                            label: "ID",
                        },
                        {
                            key: "name",
                            label: "Name",
                        },
                        {
                            key: "type",
                            label: "Type",
                        },
                        {
                            key: "plantingDate",
                            label: "Planting Date",
                        },
                    ]}
                />

                <DataTable
                    title="Parcels"
                    rows={parcels}
                    emptyMessage="No parcels found."
                    onEdit={(row) =>
                        startEdit(
                            "parcel",
                            row,
                        )
                    }
                    onDelete={(id) =>
                        deleteRecord(
                            "parcel",
                            id,
                        )
                    }
                    columns={[
                        {
                            key: "id",
                            label: "ID",
                        },
                        {
                            key: "location",
                            label: "Location",
                        },
                        {
                            key: "size",
                            label: "Size",
                        },
                        {
                            key: "soilType",
                            label: "Soil Type",
                        },
                    ]}
                />

                <DataTable
                    title="Activities"
                    rows={activities}
                    emptyMessage="No activities found."
                    onEdit={(row) =>
                        startEdit(
                            "activity",
                            row,
                        )
                    }
                    onDelete={(id) =>
                        deleteRecord(
                            "activity",
                            id,
                        )
                    }
                    columns={[
                        {
                            key: "id",
                            label: "ID",
                        },
                        {
                            key: "description",
                            label: "Description",
                        },
                        {
                            key: "type",
                            label: "Type",
                        },
                        {
                            key: "date",
                            label: "Date",
                        },
                    ]}
                />
            </section>
        </main>
    );
}