import { Navigate, useLocation } from "react-router-dom";
import { getCurrentUser } from "../../../api";

export default function ProtectedRoute({ children, requiredRole }) {
    const location = useLocation();
    const currentUser = getCurrentUser();

    if (!currentUser) {
        return (
            <Navigate
                to="/auth/login"
                replace
                state={{ from: location }}
            />
        );
    }

    if (requiredRole && currentUser.role !== requiredRole) {
        return <Navigate to="/dashboard" replace />;
    }

    return children;
}