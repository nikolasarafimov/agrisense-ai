export const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export function saveCurrentUser(user) {
    localStorage.setItem(
        "currentUser",
        JSON.stringify(user),
    );
}

export function getCurrentUser() {
    const storedUser =
        localStorage.getItem("currentUser");

    if (!storedUser) {
        return null;
    }

    try {
        return JSON.parse(storedUser);
    } catch (error) {
        console.error(
            "Could not parse current user from localStorage.",
            error,
        );

        clearCurrentUser();
        return null;
    }
}

export function getAuthToken() {
    return getCurrentUser()?.token ?? null;
}

export function clearCurrentUser() {
    localStorage.removeItem("currentUser");
}

export async function apiRequest(
    path,
    options = {},
) {
    const token =
        getAuthToken();

    const {
        headers: customHeaders = {},
        ...requestOptions
    } = options;

    const headers = {
        "Content-Type": "application/json",
        ...customHeaders,
    };

    if (token) {
        headers.Authorization =
            `Bearer ${token}`;
    }

    const response =
        await fetch(
            `${API_BASE_URL}${path}`,
            {
                ...requestOptions,
                headers,
            },
        );

    if (!response.ok) {
        if (response.status === 401) {
            clearCurrentUser();
        }

        let message =
            "Request failed.";

        try {
            const errorData =
                await response.json();

            message =
                errorData.message
                || errorData.error
                || message;
        } catch {
            const responseText =
                await response.text();

            if (responseText) {
                message = responseText;
            }
        }

        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }

    const contentType =
        response.headers.get(
            "content-type",
        );

    if (
        contentType
        && contentType.includes(
            "application/json",
        )
    ) {
        return response.json();
    }

    return response.text();
}