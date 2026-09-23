import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";

import TaskList from "./TaskList";
import { AuthProvider } from "../contexts/AuthContext";
import type { Task } from "../types/Task";

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
        status: "COMPLETED",
        priority: "LOW",
        dueDate: "2026-10-15",
    },
];

function renderTaskList(taskList: Task[] = tasks) {
    return render(
        <MemoryRouter>
            <AuthProvider>
                <TaskList
                    tasks={taskList}
                    onUpdate={vi.fn().mockResolvedValue(undefined)}
                    onDelete={vi.fn().mockResolvedValue(undefined)}
                />
            </AuthProvider>
        </MemoryRouter>
    );
}

describe("TaskList", () => {
    it("renders the task list title", () => {
        renderTaskList();

        expect(
            screen.getByRole("heading", {
                name: "My tasks",
            })
        ).toBeInTheDocument();
    });

    it("renders all provided tasks", () => {
        renderTaskList();

        expect(
            screen.getByRole("heading", {
                name: "First task",
            })
        ).toBeInTheDocument();

        expect(
            screen.getByRole("heading", {
                name: "Second task",
            })
        ).toBeInTheDocument();

        expect(
            screen.getByText("First description")
        ).toBeInTheDocument();

        expect(
            screen.getByText("Second description")
        ).toBeInTheDocument();
    });

    it("renders an empty grid when there are no tasks", () => {
        renderTaskList([]);

        expect(
            screen.getByRole("heading", {
                name: "My tasks",
            })
        ).toBeInTheDocument();

        expect(
            screen.queryByRole("heading", {
                name: "First task",
            })
        ).not.toBeInTheDocument();

        expect(
            screen.queryByRole("heading", {
                name: "Second task",
            })
        ).not.toBeInTheDocument();
    });
});