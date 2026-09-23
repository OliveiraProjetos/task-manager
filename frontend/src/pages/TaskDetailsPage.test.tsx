import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";

import TaskDetailsPage from "./TaskDetailsPage";
import { AuthProvider } from "../contexts/AuthContext";
import { getTask, updateTask } from "../services/taskService";
import type { Task } from "../types/Task";

vi.mock("../services/taskService", () => ({
    getTask: vi.fn(),
    updateTask: vi.fn(),
}));

const task: Task = {
    id: "1",
    title: "Test task",
    description: "Test description",
    status: "IN_PROGRESS",
    priority: "HIGH",
    dueDate: "2026-09-30",
};

function renderTaskDetailsPage() {
    localStorage.setItem("token", "test-token");

    return render(
        <MemoryRouter initialEntries={["/tasks/1"]}>
            <AuthProvider>
                <Routes>
                    <Route
                        path="/tasks/:id"
                        element={<TaskDetailsPage />}
                    />
                    <Route
                        path="/tasks"
                        element={<p>Tasks page</p>}
                    />
                </Routes>
            </AuthProvider>
        </MemoryRouter>
    );
}

describe("TaskDetailsPage", () => {
    it("shows the loading state while loading the task", () => {
        vi.mocked(getTask).mockReturnValue(
            new Promise(() => {})
        );

        renderTaskDetailsPage();

        expect(
            screen.getByText("Loading task...")
        ).toBeInTheDocument();
    });

    it("loads and renders the task", async () => {
        vi.mocked(getTask).mockResolvedValue(task);

        renderTaskDetailsPage();

        await waitFor(() => {
            expect(
                screen.getByRole("heading", {
                    name: "Test task",
                })
            ).toBeInTheDocument();
        });

        expect(
            screen.getByText("Test description")
        ).toBeInTheDocument();

        expect(
            screen.getByText("Status: IN_PROGRESS")
        ).toBeInTheDocument();

        expect(
            screen.getByText("Priority: HIGH")
        ).toBeInTheDocument();

        expect(
            screen.getByText("Due: 2026-09-30")
        ).toBeInTheDocument();

        expect(getTask).toHaveBeenCalledWith(
            "1",
            "test-token"
        );
    });

    it("shows an error when the task is not found", async () => {
        vi.mocked(getTask).mockRejectedValue(
            new Error("TASK_NOT_FOUND")
        );

        renderTaskDetailsPage();

        await waitFor(() => {
            expect(
                screen.getByText("Task not found")
            ).toBeInTheDocument();
        });
    });

    it("enters edit mode and saves the updated task", async () => {
        const user = userEvent.setup();

        vi.mocked(getTask).mockResolvedValue(task);

        const updatedTask: Task = {
            ...task,
            title: "Updated task",
            description: "Updated description",
            dueDate: "2026-10-15",
        };

        vi.mocked(updateTask).mockResolvedValue(updatedTask);

        renderTaskDetailsPage();

        await waitFor(() => {
            expect(
                screen.getByRole("heading", {
                    name: "Test task",
                })
            ).toBeInTheDocument();
        });

        await user.click(
            screen.getByRole("button", {
                name: "Edit",
            })
        );

        expect(
            screen.getByLabelText("Title")
        ).toHaveValue("Test task");

        await user.clear(screen.getByLabelText("Title"));
        await user.type(
            screen.getByLabelText("Title"),
            "Updated task"
        );

        await user.clear(
            screen.getByLabelText("Description")
        );
        await user.type(
            screen.getByLabelText("Description"),
            "Updated description"
        );

        const dueDate = screen.getByLabelText("Due date");

        await waitFor(() => {
            expect(dueDate).toHaveValue("2026-09-30");
        });

        dueDate.focus();

        await user.click(
            screen.getByRole("button", {
                name: "Save",
            })
        );

        await waitFor(() => {
            expect(updateTask).toHaveBeenCalledWith(
                "1",
                {
                    title: "Updated task",
                    description: "Updated description",
                    dueDate: "2026-09-30",
                    status: "IN_PROGRESS",
                    priority: "HIGH",
                },
                "test-token"
            );
        });

        expect(
            screen.getByRole("heading", {
                name: "Updated task",
            })
        ).toBeInTheDocument();
    });

    it("navigates back to the tasks page", async () => {
        const user = userEvent.setup();

        vi.mocked(getTask).mockResolvedValue(task);

        renderTaskDetailsPage();

        await waitFor(() => {
            expect(
                screen.getByRole("heading", {
                    name: "Test task",
                })
            ).toBeInTheDocument();
        });

        await user.click(
            screen.getByRole("link", {
                name: "Back to tasks",
            })
        );

        expect(
            screen.getByText("Tasks page")
        ).toBeInTheDocument();
    });
});