import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import TaskForm from "./TaskForm";
import { AuthProvider } from "../contexts/AuthContext";
import { createTask } from "../services/taskService";

vi.mock("../services/taskService", () => ({
    createTask: vi.fn(),
}));

describe("TaskForm", () => {
    it("shows validation errors when submitted empty", async () => {
        const user = userEvent.setup();

        render(
            <AuthProvider>
                <TaskForm
                    token="test-token"
                    onTaskCreated={vi.fn()}
                />
            </AuthProvider>
        );

        const submitButton = screen.getByRole("button", {
            name: /add task/i,
        });

        await user.click(submitButton);

        expect(
            screen.getByText("Title is required")
        ).toBeInTheDocument();

        expect(
            screen.getByText("Description is required")
        ).toBeInTheDocument();

        expect(
            screen.getByText("Due date is required")
        ).toBeInTheDocument();
    });

    it("creates a task when valid data is submitted", async () => {
        const user = userEvent.setup();
        const onTaskCreated = vi.fn();

        const createdTask = {
            id: "1",
            title: "Test task",
            description: "Test description",
            status: "PENDING" as const,
            priority: "MEDIUM" as const,
            dueDate: "2026-09-30",
        };

        vi.mocked(createTask).mockResolvedValue(createdTask);

        render(
            <AuthProvider>
                <TaskForm
                    token="test-token"
                    onTaskCreated={onTaskCreated}
                />
            </AuthProvider>
        );

        await user.type(
            screen.getByLabelText("Title"),
            "Test task"
        );

        await user.type(
            screen.getByLabelText("Description"),
            "Test description"
        );

        fireEvent.change(
            screen.getByLabelText("Due date"),
            {
                target: {
                    value: "2026-09-30",
                },
            }
        );

        await user.click(
            screen.getByRole("button", {
                name: /add task/i,
            })
        );

        await waitFor(() => {
            expect(createTask).toHaveBeenCalledWith(
                {
                    title: "Test task",
                    description: "Test description",
                    dueDate: "2026-09-30",
                },
                "test-token"
            );
        });

        expect(onTaskCreated).toHaveBeenCalledWith(createdTask);
    });
});