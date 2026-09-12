import { useEffect, useState } from "react";

import {
    apiRequest,
    getCurrentUser,
} from "../api";


async function fetchAdminData() {
    const [
        users,
        crops,
        parcels,
        activities,
    ] = await Promise.all([
        apiRequest("/api/admin/users"),
        apiRequest("/api/admin/crops"),
        apiRequest("/api/admin/parcels"),
        apiRequest("/api/admin/activities"),
    ]);

    return {
        users,
        crops,
        parcels,
        activities,
    };
}


function AdminTable({
                        title,
                        rows,
                        columns,
                        onDelete,
                        canDelete = () => true,
                    }) {
    return (
        <section className="dashboard-table-card">
            <h3>{title}</h3>

            {rows.length === 0 ? (
                <p className="empty-table-message">
                    No records found.
                </p>
            ) : (
                <div className="responsive-table">
                    <table>
                        <thead>
                        <tr>
                            {columns.map((column) => (
                                <th key={column.key}>
                                    {column.label}
                                </th>
                            ))}
                            <th>Action</th>
                        </tr>
                        </thead>

                        <tbody>
                        {rows.map((row) => {
                            const deletable =
                                canDelete(row);

                            return (
                                <tr key={row.id}>
                                    {columns.map((column) => (
                                        <td key={column.key}>
                                            {column.render
                                                ? column.render(row)
                                                : row[column.key] ?? "-"}
                                        </td>
                                    ))}

                                    <td>
                                        <button
                                            className="admin-delete-button"
                                            type="button"
                                            disabled={!deletable}
                                            onClick={() =>
                                                onDelete(row.id)
                                            }
                                        >
                                            {deletable
                                                ? "Delete"
                                                : "Current account"}
                                        </button>
                                    </td>
                                </tr>
                            );
                        })}
                        </tbody>
                    </table>
                </div>
            )}
        </section>
    );
}


export default function AdminPage() {
    const currentUser = getCurrentUser();
    const isAdmin =
        currentUser?.role === "ADMIN";

    const [users, setUsers] =
        useState([]);

    const [crops, setCrops] =
        useState([]);

    const [parcels, setParcels] =
        useState([]);

    const [activities, setActivities] =
        useState([]);

    const [status, setStatus] =
        useState(() => ({
            loading: isAdmin,
            message: isAdmin
                ? "Loading admin data..."
                : "",
            type: isAdmin
                ? "info"
                : "",
        }));


    useEffect(() => {
        if (!isAdmin) {
            return;
        }

        let cancelled = false;

        const loadInitialData = async () => {
            try {
                const data =
                    await fetchAdminData();

                if (cancelled) {
                    return;
                }

                setUsers(data.users);
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
                        || "Could not load admin data.",
                    type: "error",
                });
            }
        };

        loadInitialData();

        return () => {
            cancelled = true;
        };
    }, [isAdmin]);


    const deleteRecord = async (
        resource,
        id,
    ) => {
        const confirmed =
            window.confirm(
                `Are you sure you want to delete this ${resource} record?`,
            );

        if (!confirmed) {
            return;
        }

        setStatus({
            loading: true,
            message: "Deleting record...",
            type: "info",
        });

        try {
            await apiRequest(
                `/api/admin/${resource}/${id}`,
                {
                    method: "DELETE",
                },
            );

            const data =
                await fetchAdminData();

            setUsers(data.users);
            setCrops(data.crops);
            setParcels(data.parcels);
            setActivities(data.activities);

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


    if (!currentUser) {
        return (
            <main className="dashboard-page">
                <section className="dashboard-hero">
                    <span className="section-label">
                        Access Restricted
                    </span>

                    <h1>Admin Panel</h1>

                    <p>
                        You must be logged in to access
                        the administrative panel.
                    </p>
                </section>
            </main>
        );
    }


    if (!isAdmin) {
        return (
            <main className="dashboard-page">
                <section className="dashboard-hero">
                    <span className="section-label">
                        Access Denied
                    </span>

                    <h1>Admin Panel</h1>

                    <p>
                        This page is available only for
                        users with the ADMIN role. Your
                        current role is{" "}
                        <strong>
                            {currentUser.role}
                        </strong>.
                    </p>
                </section>
            </main>
        );
    }


    return (
        <main className="dashboard-page">
            <section className="dashboard-hero">
                <span className="section-label">
                    Administrative Functionalities
                </span>

                <h1>Admin Panel</h1>

                <p>
                    Review users, crops, parcels,
                    and agricultural activities,
                    and perform administrative
                    delete operations when needed.
                </p>

                <div className="current-user-box">
                    Current user:{" "}
                    <strong>
                        {currentUser.fullName}
                    </strong>
                    {" · "}
                    Role: {currentUser.role}
                </div>
            </section>

            {status.message && (
                <div
                    className={
                        `dashboard-message ${status.type}`
                    }
                >
                    {status.message}
                </div>
            )}

            <section className="dashboard-data-grid">
                <AdminTable
                    title="Users"
                    rows={users}
                    onDelete={(id) =>
                        deleteRecord(
                            "users",
                            id,
                        )
                    }
                    canDelete={(row) =>
                        row.id !== currentUser.id
                    }
                    columns={[
                        {
                            key: "id",
                            label: "ID",
                        },
                        {
                            key: "fullName",
                            label: "Full Name",
                        },
                        {
                            key: "email",
                            label: "Email",
                        },
                        {
                            key: "role",
                            label: "Role",
                        },
                    ]}
                />

                <AdminTable
                    title="Crops"
                    rows={crops}
                    onDelete={(id) =>
                        deleteRecord(
                            "crops",
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
                        {
                            key: "user",
                            label: "User",
                            render: (row) =>
                                row.user?.email
                                ?? "-",
                        },
                    ]}
                />

                <AdminTable
                    title="Parcels"
                    rows={parcels}
                    onDelete={(id) =>
                        deleteRecord(
                            "parcels",
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
                        {
                            key: "user",
                            label: "User",
                            render: (row) =>
                                row.user?.email
                                ?? "-",
                        },
                    ]}
                />

                <AdminTable
                    title="Activities"
                    rows={activities}
                    onDelete={(id) =>
                        deleteRecord(
                            "activities",
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
                        {
                            key: "user",
                            label: "User",
                            render: (row) =>
                                row.user?.email
                                ?? "-",
                        },
                    ]}
                />
            </section>

            {status.loading && (
                <p className="empty-table-message">
                    Please wait...
                </p>
            )}
        </main>
    );
}