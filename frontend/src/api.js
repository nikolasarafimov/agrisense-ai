const configuredApiBaseUrl =
    import.meta.env.VITE_API_BASE_URL?.trim();

export const API_BASE_URL =
    (
        configuredApiBaseUrl
        || "http://localhost:8080"
    ).replace(/\/+$/, "");


const CURRENT_USER_KEY =
    "currentUser";


export function saveCurrentUser(user) {
    if (
        !user
        || typeof user !== "object"
    ) {
        clearCurrentUser();
        return;
    }

    const existingUser =
        getCurrentUser();

    const userToStore = {
        ...user,
        ...(
            !user.token
            && existingUser?.token
                ? {
                    token:
                    existingUser.token,
                }
                : {}
        ),
    };

    localStorage.setItem(
        CURRENT_USER_KEY,
        JSON.stringify(
            userToStore,
        ),
    );
}


export function getCurrentUser() {
    const storedUser =
        localStorage.getItem(
            CURRENT_USER_KEY,
        );

    if (!storedUser) {
        return null;
    }

    try {
        const parsedUser =
            JSON.parse(
                storedUser,
            );

        if (
            !parsedUser
            || typeof parsedUser !== "object"
        ) {
            clearCurrentUser();
            return null;
        }

        return parsedUser;

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
    return (
        getCurrentUser()?.token
        ?? null
    );
}


export function clearCurrentUser() {
    localStorage.removeItem(
        CURRENT_USER_KEY,
    );
}


async function readErrorMessage(
    response,
) {
    let responseText = "";

    try {
        responseText =
            await response.text();
    } catch {
        return "Request failed.";
    }

    if (!responseText) {
        return "Request failed.";
    }

    const contentType =
        response.headers.get(
            "content-type",
        ) || "";

    if (
        contentType.includes(
            "application/json",
        )
    ) {
        try {
            const errorData =
                JSON.parse(
                    responseText,
                );

            return (
                errorData.message
                || errorData.error
                || "Request failed."
            );
        } catch {
            return "Request failed.";
        }
    }

    return responseText;
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
        ...customHeaders,
    };

    const hasBody =
        requestOptions.body !== undefined
        && requestOptions.body !== null;

    const isFormData =
        typeof FormData !== "undefined"
        && requestOptions.body
        instanceof FormData;

    const hasContentType =
        Object.keys(headers)
            .some(
                (headerName) =>
                    headerName.toLowerCase()
                    === "content-type",
            );

    if (
        hasBody
        && !isFormData
        && !hasContentType
    ) {
        headers["Content-Type"] =
            "application/json";
    }

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
        if (
            response.status === 401
        ) {
            clearCurrentUser();
        }

        const message =
            await readErrorMessage(
                response,
            );

        throw new Error(
            message,
        );
    }

    if (
        response.status === 204
    ) {
        return null;
    }

    const contentType =
        response.headers.get(
            "content-type",
        ) || "";

    if (
        contentType.includes(
            "application/json",
        )
    ) {
        return response.json();
    }

    return response.text();
}