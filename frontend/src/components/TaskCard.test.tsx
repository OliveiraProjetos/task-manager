import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";
import TaskCard from "./TaskCard";
import { AuthProvider } from "../contexts/AuthContext";
import type { Task, UpdateTaskRequest } from "../types/Task";

const task: Task = {
    id: "1",
    title: "Test task",
    description: "Test description",
    status: "IN_PROGRESS",
    priority: "HIGH",
    dueDate: "2026-09-30",
};

function renderTaskCard() {
    const onUpdate = vi.fn<
        (id: string, data: UpdateTaskRequest) => Promise<void>
    >().mockResolvedValue(undefined);

    const onDelete = vi.fn<(id: string) => Promise<void>>()
        .mockResolvedValue(undefined);

    const user = userEvent.setup();

    render(
        <MemoryRouter>
            <AuthProvider>
                <TaskCard
                    task={task}
                    onUpdate={onUpdate}
                    onDelete={onDelete}
                />
            </AuthProvider>
        </MemoryRouter>
    );

    return { user, onUpdate, onDelete };
}

describe("TaskCard", () => {
    it("renders task information", () => {
        renderTaskCard();

        expect(
            screen.getByRole("heading", { name: "Test task" })
        ).toBeInTheDocument();

        expect(screen.getByText("Test description")).toBeInTheDocument();
        expect(screen.getByText("Status: IN_PROGRESS")).toBeInTheDocument();
        expect(screen.getByText("Priority: HIGH")).toBeInTheDocument();
        expect(screen.getByText("Due: 2026-09-30")).toBeInTheDocument();
    });

    it("renders the edit button", () => {
        renderTaskCard();

        expect(
            screen.getByRole("button", { name: /edit/i })
        ).toBeInTheDocument();
    });

    it("renders the delete button", () => {
        renderTaskCard();

        expect(
            screen.getByRole("button", { name: /delete/i })
        ).toBeInTheDocument();
    });

    it("sends the updated status and priority when editing a task", async () => {
        const { user, onUpdate } = renderTaskCard();

        await user.click(screen.getByRole("button", { name: /^edit$/i }));

        await user.selectOptions(
            screen.getByLabelText("Status"),
            "COMPLETED"
        );

        await user.selectOptions(
            screen.getByLabelText("Priority"),
            "MEDIUM"
        );

        await user.click(screen.getByRole("button", { name: /^save$/i }));

        await waitFor(() => {
            expect(onUpdate).toHaveBeenCalledWith("1", {
                title: "Test task",
                description: "Test description",
                status: "COMPLETED",
                priority: "MEDIUM",
                dueDate: "2026-09-30",
            });
        });
    });
});