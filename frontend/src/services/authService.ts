import type {LoginRequest, LoginResponse, RegisterRequest} from "../types/Auth";

const API_URL = "http://localhost:8080";

export async function login(
    credentials: LoginRequest
): Promise<LoginResponse> {
    const response = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials),
    });

    if (!response.ok) {
        throw new Error("Invalid username or password");
    }

    return response.json();
}

export async function register(
    credentials: RegisterRequest
): Promise<void> {
    const response = await fetch(`${API_URL}/auth/register`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials),
    });

    if (!response.ok) {
        throw new Error("Failed to register");
    }
}