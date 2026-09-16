import { apiRequest } from "./apiClient";
import type { CreateTaskRequest, Task } from "../types/Task";

export async function getTasks(token: string): Promise<Task[]> {
    const response = await apiRequest("/tasks", {}, token);

    if (!response.ok) {
        throw new Error("Failed to fetch tasks");
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
        throw new Error("Failed to create task");
    }

    return response.json();
}

export async function updateTask(
    id: string,
    task: CreateTaskRequest,
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
        throw new Error("Failed to update task");
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
        throw new Error("Failed to delete task");
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

    if (response.status === 404) {
        throw new Error("TASK_NOT_FOUND");
    }

    if (!response.ok) {
        throw new Error("Failed to fetch task");
    }

    return response.json();
}