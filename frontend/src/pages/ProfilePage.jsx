import { useEffect, useState } from "react";
import { apiRequest, getCurrentUser, saveCurrentUser } from "../api";

export default function ProfilePage() {
    const currentUser = getCurrentUser();

    const [formData, setFormData] = useState({
        fullName: "",
        email: "",
        password: "",
        role: "USER",
    });

    const [status, setStatus] = useState({
        loading: false,
        message: "",
        type: "",
    });

    useEffect(() => {
        if (!currentUser?.id) {
            setStatus({
                loading: false,
                message: "No logged-in user found. Please log in first.",
                type: "error",
            });
            return;
        }

        const loadUser = async () => {
            setStatus({
                loading: true,
                message: "Loading profile data...",
                type: "info",
            });

            try {
                const user = await apiRequest(`/api/users/${currentUser.id}`);

                setFormData({
                    fullName: user.fullName || "",
                    email: user.email || "",
                    password: "",
                    role: user.role || "USER",
                });

            } catch (error) {
                setStatus({
                    loading: false,
                    message: error.message || "Could not load profile data.",
                    type: "error",
                });
            }
        };

        loadUser();
    }, [currentUser?.id]);

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((previousData) => ({
            ...previousData,
            [name]: value,
        }));
    };

    const validateForm = () => {
        if (!formData.fullName.trim()) {
            return "Full name is required.";
        }

        if (!formData.email.trim()) {
            return "Email is required.";
        }

        if (!formData.email.includes("@")) {
            return "Please enter a valid email address.";
        }

        if (formData.password && formData.password.length < 6) {
            return "Password must contain at least 6 characters.";
        }

        return "";
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!currentUser?.id) {
            setStatus({
                loading: false,
                message: "No logged-in user found. Please log in first.",
                type: "error",
            });
            return;
        }

        const validationError = validateForm();

        if (validationError) {
            setStatus({
                loading: false,
                message: validationError,
                type: "error",
            });
            return;
        }

        setStatus({
            loading: true,
            message: "Updating profile...",
            type: "info",
        });

        const payload = {
            fullName: formData.fullName,
            email: formData.email,
        };

        if (formData.password.trim()) {
            payload.password = formData.password;
        }

        try {
            const updatedUser = await apiRequest(`/api/users/${currentUser.id}`, {
                method: "PUT",
                body: JSON.stringify(payload),
            });

            const storedUser = {
                id: updatedUser.id,
                fullName: updatedUser.fullName,
                email: updatedUser.email,
                role: updatedUser.role,
                message: "Profile updated successfully.",
            };

            saveCurrentUser(storedUser);

            setFormData((previousData) => ({
                ...previousData,
                password: "",
            }));

            setStatus({
                loading: false,
                message: "Profile updated successfully.",
                type: "success",
            });
        } catch (error) {
            setStatus({
                loading: false,
                message: error.message || "Could not update profile.",
                type: "error",
            });
        }
    };

    return (
        <main className="page-shell">
            <section className="page-header-card">
                <span className="section-label">User Profile</span>
                <h1>Profile Settings</h1>
                <p>
                    View and update basic user information. This page demonstrates
                    user profile management functionality required by the project
                    specification.
                </p>
            </section>

            <section className="form-layout">
                <form className="data-form" onSubmit={handleSubmit}>
                    <div className="form-field">
                        <label htmlFor="fullName">Full Name</label>
                        <input
                            id="fullName"
                            name="fullName"
                            type="text"
                            value={formData.fullName}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-field">
                        <label htmlFor="email">Email</label>
                        <input
                            id="email"
                            name="email"
                            type="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-field">
                        <label htmlFor="password">New Password optional</label>
                        <input
                            id="password"
                            name="password"
                            type="password"
                            placeholder="Leave empty to keep current password"
                            value={formData.password}
                            onChange={handleChange}
                            minLength={6}
                        />
                    </div>

                    <div className="form-field">
                        <label htmlFor="role">Role</label>
                        <input
                            id="role"
                            name="role"
                            type="text"
                            value={formData.role}
                            onChange={handleChange}
                            readOnly
                        />
                    </div>

                    <button className="submit-button" type="submit" disabled={status.loading}>
                        {status.loading ? "Saving..." : "Save Profile"}
                    </button>

                    {status.message && (
                        <div className={`form-alert ${status.type}`}>
                            {status.message}
                        </div>
                    )}
                </form>

                <aside className="preview-panel">
                    <h2>Current User</h2>

                    {currentUser ? (
                        <div className="preview-list">
                            <div>
                                <span>User ID</span>
                                <strong>{currentUser.id}</strong>
                            </div>

                            <div>
                                <span>Full Name</span>
                                <strong>{formData.fullName || "-"}</strong>
                            </div>

                            <div>
                                <span>Email</span>
                                <strong>{formData.email || "-"}</strong>
                            </div>

                            <div>
                                <span>Role</span>
                                <strong>{formData.role || "-"}</strong>
                            </div>
                        </div>
                    ) : (
                        <p>No logged-in user found.</p>
                    )}
                </aside>
            </section>
        </main>
    );
}