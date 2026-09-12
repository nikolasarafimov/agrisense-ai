import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { apiRequest, saveCurrentUser } from "../../../api";

function LoginForm() {
    const navigate = useNavigate();

    const location = useLocation();

    const redirectPath =
        location.state?.from?.pathname || "/dashboard";

    const [formData, setFormData] = useState({
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

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!formData.email.trim() || !formData.password.trim()) {
            setStatus({
                message: "Please enter both email and password.",
                type: "error",
            });
            return;
        }

        setLoading(true);
        setStatus({
            message: "Signing in...",
            type: "info",
        });

        try {
            const user = await apiRequest("/api/users/login", {
                method: "POST",
                body: JSON.stringify(formData),
            });

            saveCurrentUser(user);

            setStatus({
                message: "Login successful. Redirecting to dashboard...",
                type: "success",
            });

            setTimeout(() => {
                navigate(redirectPath, { replace: true });
            }, 700);
        } catch (error) {
            setStatus({
                message: error.message || "Login failed. Please check your credentials.",
                type: "error",
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <form className="auth-form" onSubmit={handleSubmit}>
            <label htmlFor="login-email">Email</label>
            <input
                id="login-email"
                type="email"
                name="email"
                placeholder="you@example.com"
                value={formData.email}
                onChange={handleChange}
                required
            />

            <label htmlFor="login-password">Password</label>
            <input
                id="login-password"
                type="password"
                name="password"
                placeholder="Enter your password"
                value={formData.password}
                onChange={handleChange}
                required
            />

            {status.message && (
                <div className={`auth-message ${status.type}`}>
                    {status.message}
                </div>
            )}

            <button type="submit" disabled={loading}>
                {loading ? "Logging in..." : "Login"}
            </button>
        </form>
    );
}

export default LoginForm;