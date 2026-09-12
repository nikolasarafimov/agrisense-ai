import {
    useEffect,
    useState,
} from "react";

import {
    apiRequest,
    getCurrentUser,
    saveCurrentUser,
} from "../api";


export default function ProfilePage() {
    const [
        currentUser,
        setCurrentUser,
    ] =
        useState(() => getCurrentUser());

    const currentUserId =
        currentUser?.id ?? null;

    const [formData, setFormData] =
        useState(() => ({
            fullName:
                currentUser?.fullName ?? "",
            email:
                currentUser?.email ?? "",
            password: "",
            role:
                currentUser?.role ?? "USER",
        }));

    const [isLoading, setIsLoading] =
        useState(Boolean(currentUserId));

    const [isSaving, setIsSaving] =
        useState(false);

    const [status, setStatus] =
        useState(() => ({
            message: currentUserId
                ? "Loading profile data..."
                : "No logged-in user found. Please log in first.",
            type: currentUserId
                ? "info"
                : "error",
        }));


    useEffect(() => {
        if (!currentUserId) {
            return;
        }

        let cancelled = false;

        const loadProfile = async () => {
            try {
                const user =
                    await apiRequest(
                        "/api/users/me",
                    );

                if (cancelled) {
                    return;
                }

                setFormData({
                    fullName:
                        user.fullName ?? "",
                    email:
                        user.email ?? "",
                    password: "",
                    role:
                        user.role ?? "USER",
                });

                setIsLoading(false);

                setStatus({
                    message: "",
                    type: "",
                });
            } catch (error) {
                if (cancelled) {
                    return;
                }

                setIsLoading(false);

                setStatus({
                    message:
                        error.message
                        || "Could not load profile data.",
                    type: "error",
                });
            }
        };

        loadProfile();

        return () => {
            cancelled = true;
        };
    }, [currentUserId]);


    const handleChange = (event) => {
        const {
            name,
            value,
        } =
            event.target;

        setFormData(
            (previousData) => ({
                ...previousData,
                [name]: value,
            }),
        );
    };


    const validateForm = () => {
        const fullName =
            formData.fullName.trim();

        const email =
            formData.email.trim();

        if (!fullName) {
            return "Full name is required.";
        }

        if (!email) {
            return "Email is required.";
        }

        if (!email.includes("@")) {
            return "Please enter a valid email address.";
        }

        if (
            formData.password
            && formData.password.length < 6
        ) {
            return "Password must contain at least 6 characters.";
        }

        return "";
    };


    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!currentUserId) {
            setStatus({
                message:
                    "No logged-in user found. Please log in first.",
                type: "error",
            });

            return;
        }

        const validationError =
            validateForm();

        if (validationError) {
            setStatus({
                message:
                validationError,
                type: "error",
            });

            return;
        }

        const payload = {
            fullName:
                formData.fullName.trim(),
            email:
                formData.email.trim(),
        };

        if (formData.password.trim()) {
            payload.password =
                formData.password;
        }

        setIsSaving(true);

        setStatus({
            message:
                "Updating profile...",
            type: "info",
        });

        try {
            const updatedUser =
                await apiRequest(
                    "/api/users/me",
                    {
                        method: "PUT",
                        body:
                            JSON.stringify(
                                payload,
                            ),
                    },
                );

            saveCurrentUser(
                updatedUser,
            );

            setCurrentUser(
                updatedUser,
            );

            setFormData({
                fullName:
                    updatedUser.fullName ?? "",
                email:
                    updatedUser.email ?? "",
                password: "",
                role:
                    updatedUser.role ?? "USER",
            });

            setStatus({
                message:
                    "Profile updated successfully.",
                type: "success",
            });
        } catch (error) {
            setStatus({
                message:
                    error.message
                    || "Could not update profile.",
                type: "error",
            });
        } finally {
            setIsSaving(false);
        }
    };


    const formDisabled =
        isLoading || isSaving;


    return (
        <main className="page-shell">
            <section className="page-header-card">
                <span className="section-label">
                    User Profile
                </span>

                <h1>
                    Profile Settings
                </h1>

                <p>
                    View and update your account
                    information and password.
                </p>
            </section>


            <section className="form-layout">
                <form
                    className="data-form"
                    onSubmit={handleSubmit}
                >
                    <div className="form-field">
                        <label htmlFor="fullName">
                            Full Name
                        </label>

                        <input
                            id="fullName"
                            name="fullName"
                            type="text"
                            value={
                                formData.fullName
                            }
                            disabled={formDisabled}
                            onChange={handleChange}
                            required
                        />
                    </div>


                    <div className="form-field">
                        <label htmlFor="email">
                            Email
                        </label>

                        <input
                            id="email"
                            name="email"
                            type="email"
                            value={
                                formData.email
                            }
                            disabled={formDisabled}
                            onChange={handleChange}
                            required
                        />
                    </div>


                    <div className="form-field">
                        <label htmlFor="password">
                            New Password
                            {" "}
                            <span>
                                (optional)
                            </span>
                        </label>

                        <input
                            id="password"
                            name="password"
                            type="password"
                            placeholder="Leave empty to keep current password"
                            value={
                                formData.password
                            }
                            disabled={formDisabled}
                            onChange={handleChange}
                            minLength={6}
                        />
                    </div>


                    <div className="form-field">
                        <label htmlFor="role">
                            Role
                        </label>

                        <input
                            id="role"
                            name="role"
                            type="text"
                            value={formData.role}
                            readOnly
                        />
                    </div>


                    <button
                        className="submit-button"
                        type="submit"
                        disabled={formDisabled}
                    >
                        {isLoading
                            ? "Loading..."
                            : isSaving
                                ? "Saving..."
                                : "Save Profile"}
                    </button>


                    {status.message && (
                        <div
                            className={
                                `form-alert ${status.type}`
                            }
                        >
                            {status.message}
                        </div>
                    )}
                </form>


                <aside className="preview-panel">
                    <h2>
                        Current User
                    </h2>

                    {currentUser ? (
                        <div className="preview-list">
                            <div>
                                <span>
                                    User ID
                                </span>

                                <strong>
                                    {currentUser.id}
                                </strong>
                            </div>

                            <div>
                                <span>
                                    Full Name
                                </span>

                                <strong>
                                    {formData.fullName
                                        || "-"}
                                </strong>
                            </div>

                            <div>
                                <span>
                                    Email
                                </span>

                                <strong>
                                    {formData.email
                                        || "-"}
                                </strong>
                            </div>

                            <div>
                                <span>
                                    Role
                                </span>

                                <strong>
                                    {formData.role
                                        || "-"}
                                </strong>
                            </div>
                        </div>
                    ) : (
                        <p>
                            No logged-in user found.
                        </p>
                    )}
                </aside>
            </section>
        </main>
    );
}