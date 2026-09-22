import { ApiError, apiRequest } from "./apiClient";
import type {
    CreateTaskRequest,
    Task,
    UpdateTaskRequest,
} from "../types/Task";

async function getErrorMessage(response: Response): Promise<string> {
    try {
        const data = await response.json();

        if (typeof data.message === "string") {
            return data.message;
        }

        if (typeof data.error === "string") {
            return data.error;
        }
    } catch {
        // Ignore invalid or empty response bodies.
    }

    return "Request failed";
}

export async function getTasks(token: string): Promise<Task[]> {
    const response = await apiRequest("/tasks", {}, token);

    if (!response.ok) {
        throw new ApiError(
            response.status,
            await getErrorMessage(response)
        );
    }

    return response.json();
}

export async function createTask(
    task: CreateTaskRequest,
    token: string
): Promise<Task> {
    const response = await apiRequest(
        "/tasks",
        {
            method: "POST",
            body: JSON.stringify(task),
        },
        token
    );

    if (!response.ok) {
        throw new ApiError(
            response.status,
            await getErrorMessage(response)
        );
    }

    return response.json();
}

export async function updateTask(
    id: string,
    task: UpdateTaskRequest,
    token: string
): Promise<Task> {
    const response = await apiRequest(
        `/tasks/${id}`,
        {
            method: "PUT",
            body: JSON.stringify(task),
        },
        token
    );

    if (!response.ok) {
        throw new ApiError(
            response.status,
            await getErrorMessage(response)
        );
    }

    return response.json();
}

export async function deleteTask(
    id: string,
    token: string
): Promise<void> {
    const response = await apiRequest(
        `/tasks/${id}`,
        {
            method: "DELETE",
        },
        token
    );

    if (!response.ok) {
        throw new ApiError(
            response.status,
            await getErrorMessage(response)
        );
    }
}

export async function getTask(
    id: string,
    token: string
): Promise<Task> {
    const response = await apiRequest(
        `/tasks/${id}`,
        {},
        token
    );

    if (!response.ok) {
        throw new ApiError(
            response.status,
            await getErrorMessage(response)
        );
    }

    return response.json();
}