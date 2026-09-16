const API_URL = "http://localhost:8080";

export async function apiRequest(
    path: string,
    options: RequestInit = {},
    token?: string
): Promise<Response> {
    const headers = new Headers(options.headers);

    if (token) {
        headers.set("Authorization", `Bearer ${token}`);
    }

    if (options.body && !headers.has("Content-Type")) {
        headers.set("Content-Type", "application/json");
    }

    const response = await fetch(`${API_URL}${path}`, {
        ...options,
        headers,
    });

    if (response.status === 401) {
        throw new Error("UNAUTHORIZED");
    }

    return response;
}