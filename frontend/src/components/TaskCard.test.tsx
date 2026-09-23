import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";
import TaskCard from "./TaskCard";
import { AuthProvider } from "../contexts/AuthContext";
import type { Task } from "../types/Task";

const task: Task = {
    id: "1",
    title: "Test task",
    description: "Test description",
    status: "IN_PROGRESS",
    priority: "HIGH",
    dueDate: "2026-09-30",
};

function renderTaskCard() {
    return render(
        <MemoryRouter>
            <AuthProvider>
                <TaskCard
                    task={task}
                    onUpdate={vi.fn()}
                    onDelete={vi.fn()}
                />
            </AuthProvider>
        </MemoryRouter>
    );
}

describe("TaskCard", () => {
    it("renders task information", () => {
        renderTaskCard();

        expect(
            screen.getByRole("heading", {
                name: "Test task",
            })
        ).toBeInTheDocument();

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
    });

    it("renders the edit button", () => {
        renderTaskCard();

        expect(
            screen.getByRole("button", {
                name: /edit/i,
            })
        ).toBeInTheDocument();
    });

    it("renders the delete button", () => {
        renderTaskCard();

        expect(
            screen.getByRole("button", {
                name: /delete/i,
            })
        ).toBeInTheDocument();
    });
});