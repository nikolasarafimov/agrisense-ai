export const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export const DEFAULT_DEMO_USER_ID =
    Number(import.meta.env.VITE_DEMO_USER_ID) || 1;

export function saveCurrentUser(user) {
    localStorage.setItem("currentUser", JSON.stringify(user));
}

export function getCurrentUser() {
    const storedUser = localStorage.getItem("currentUser");

    if (!storedUser) {
        return null;
    }

    try {
        return JSON.parse(storedUser);
    } catch (error) {
        console.error("Could not parse current user from localStorage.", error);
        localStorage.removeItem("currentUser");
        return null;
    }
}

export function getCurrentUserId() {
    const user = getCurrentUser();
    return user?.id || DEFAULT_DEMO_USER_ID;
}

export function clearCurrentUser() {
    localStorage.removeItem("currentUser");
}

export async function apiRequest(path, options = {}) {
    const response = await fetch(`${API_BASE_URL}${path}`, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {}),
        },
        ...options,
    });

    if (!response.ok) {
        let message = "Request failed.";

        try {
            const errorData = await response.json();
            message = errorData.message || errorData.error || message;
        } catch {
            message = await response.text();
        }

        throw new Error(message || "Request failed.");
    }

    const contentType = response.headers.get("content-type");

    if (contentType && contentType.includes("application/json")) {
        return response.json();
    }

    return response.text();
}