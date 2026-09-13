import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiRequest, saveCurrentUser } from "../../../api";

function RegisterForm() {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        fullName: "",
        email: "",
        password: "",
    });

    const [status, setStatus] = useState({
        message: "",
        type: "",
    });

    const [loading, setLoading] = useState(false);

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((previousData) => ({
            ...previousData,
            [name]: value,
        }));
    };

    const validateForm = () => {
        const fullName = formData.fullName.trim();
        const email = formData.email.trim();

        if (!fullName) {
            return "Full name is required.";
        }

        if (fullName.length > 100) {
            return "Full name must not exceed 100 characters.";
        }

        if (!email) {
            return "Email is required.";
        }

        if (email.length > 254) {
            return "Email must not exceed 254 characters.";
        }

        if (!email.includes("@")) {
            return "Please enter a valid email address.";
        }

        if (formData.password.length < 6) {
            return "Password must contain at least 6 characters.";
        }

        if (formData.password.length > 72) {
            return "Password must not exceed 72 characters.";
        }

        return "";
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        const validationError = validateForm();

        if (validationError) {
            setStatus({
                message: validationError,
                type: "error",
            });

            return;
        }

        setLoading(true);

        setStatus({
            message: "Creating account...",
            type: "info",
        });

        try {
            const user = await apiRequest("/api/users/register", {
                method: "POST",
                body: JSON.stringify({
                    fullName: formData.fullName.trim(),
                    email: formData.email.trim(),
                    password: formData.password,
                }),
            });

            saveCurrentUser(user);

            setStatus({
                message:
                    "Account created successfully. Redirecting to dashboard...",
                type: "success",
            });

            setTimeout(() => {
                navigate("/dashboard", {
                    replace: true,
                });
            }, 700);
        } catch (error) {
            setStatus({
                message:
                    error.message ||
                    "Registration failed.",
                type: "error",
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <form className="auth-form" onSubmit={handleSubmit}>
            <label htmlFor="register-name">Full Name</label>

            <input
                id="register-name"
                type="text"
                name="fullName"
                placeholder="Jane Farmer"
                value={formData.fullName}
                onChange={handleChange}
                autoComplete="name"
                maxLength={100}
                required
            />

            <label htmlFor="register-email">Email</label>

            <input
                id="register-email"
                type="email"
                name="email"
                placeholder="you@example.com"
                value={formData.email}
                onChange={handleChange}
                autoComplete="email"
                maxLength={254}
                required
            />

            <label htmlFor="register-password">Password</label>

            <input
                id="register-password"
                type="password"
                name="password"
                placeholder="Create a password"
                value={formData.password}
                onChange={handleChange}
                autoComplete="new-password"
                minLength={6}
                maxLength={72}
                required
            />

            {status.message && (
                <div
                    className={`auth-message ${status.type}`}
                    role={status.type === "error" ? "alert" : "status"}
                >
                    {status.message}
                </div>
            )}

            <button
                type="submit"
                disabled={loading}
            >
                {loading ? "Creating Account..." : "Create Account"}
            </button>
        </form>
    );
}

export default RegisterForm;