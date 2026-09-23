import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";
import TasksPage from "./TasksPage";
import { AuthProvider } from "../contexts/AuthContext";
import { ApiError } from "../services/apiClient";
import {deleteTask, getTasks,} from "../services/taskService";
import type { Task } from "../types/Task";

vi.mock("../services/taskService", () => ({
    getTasks: vi.fn(),
    deleteTask: vi.fn(),
    updateTask: vi.fn(),
}));

const tasks: Task[] = [
    {
        id: "1",
        title: "First task",
        description: "First description",
        status: "PENDING",
        priority: "HIGH",
        dueDate: "2026-09-30",
    },
    {
        id: "2",
        title: "Second task",
        description: "Second description",
        status: "IN_PROGRESS",
        priority: "MEDIUM",
        dueDate: "2026-10-01",
    },
];

function renderTasksPage() {
    localStorage.setItem("token", "test-token");

    return render(
        <MemoryRouter>
            <AuthProvider>
                <TasksPage />
            </AuthProvider>
        </MemoryRouter>
    );
}

describe("TasksPage", () => {
    it("loads and renders tasks", async () => {
        vi.mocked(getTasks).mockResolvedValue(tasks);

        renderTasksPage();

        expect(
            screen.getByText("Loading tasks...")
        ).toBeInTheDocument();

        await waitFor(() => {
            expect(
                screen.getByRole("heading", {
                    name: "First task",
                })
            ).toBeInTheDocument();
        });

        expect(
            screen.getByRole("heading", {
                name: "Second task",
            })
        ).toBeInTheDocument();

        expect(getTasks).toHaveBeenCalledWith("test-token");
    });

    it("shows the empty state when there are no tasks", async () => {
        vi.mocked(getTasks).mockResolvedValue([]);

        renderTasksPage();

        await waitFor(() => {
            expect(
                screen.getByRole("heading", {
                    name: "No tasks yet",
                })
            ).toBeInTheDocument();
        });

        expect(
            screen.getByText(
                "Create your first task using the form above."
            )
        ).toBeInTheDocument();
    });

    it("deletes a task and shows a success message", async () => {
        const user = userEvent.setup();

        vi.mocked(getTasks).mockResolvedValue(tasks);
        vi.mocked(deleteTask).mockResolvedValue();

        vi.spyOn(window, "confirm").mockReturnValue(true);

        renderTasksPage();

        await waitFor(() => {
            expect(
                screen.getByRole("heading", {
                    name: "First task",
                })
            ).toBeInTheDocument();
        });

        const deleteButtons = screen.getAllByRole(
            "button",
            {
                name: /delete/i,
            }
        );

        await user.click(deleteButtons[0]);

        await waitFor(() => {
            expect(
                screen.queryByRole("heading", {
                    name: "First task",
                })
            ).not.toBeInTheDocument();
        });

        expect(deleteTask).toHaveBeenCalledWith(
            "1",
            "test-token"
        );

        expect(
            screen.getByText("Task deleted successfully.")
        ).toBeInTheDocument();
    });
});

it("logs out when loading tasks returns 401", async () => {
    vi.mocked(getTasks).mockRejectedValue(
        new ApiError(401, "UNAUTHORIZED")
    );

    localStorage.setItem("token", "test-token");

    renderTasksPage();

    await waitFor(() => {
        expect(localStorage.getItem("token")).toBeNull();
    });
});

it("logs out when deleting a task returns 401", async () => {
    const user = userEvent.setup();

    vi.mocked(getTasks).mockResolvedValue(tasks);
    vi.mocked(deleteTask).mockRejectedValue(
        new ApiError(401, "UNAUTHORIZED")
    );

    vi.spyOn(window, "confirm").mockReturnValue(true);

    renderTasksPage();

    await waitFor(() => {
        expect(
            screen.getByRole("heading", {
                name: "First task",
            })
        ).toBeInTheDocument();
    });

    await user.click(
        screen.getAllByRole("button", {
            name: /delete/i,
        })[0]
    );

    await waitFor(() => {
        expect(localStorage.getItem("token")).toBeNull();
    });
});