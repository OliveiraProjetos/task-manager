import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";

import TaskEditForm from "./TaskEditForm";
import { AuthProvider } from "../contexts/AuthContext";
import type { Task } from "../types/Task";

const task: Task = {
    id: "1",
    title: "Existing task",
    description: "Existing description",
    status: "IN_PROGRESS",
    priority: "HIGH",
    dueDate: "2026-09-30",
};

function renderTaskEditForm(
    onSave = vi.fn().mockResolvedValue(undefined),
    onCancel = vi.fn()
) {
    return render(
        <AuthProvider>
            <TaskEditForm
                task={task}
                onSave={onSave}
                onCancel={onCancel}
            />
        </AuthProvider>
    );
}

describe("TaskEditForm", () => {
    it("renders the current task values", () => {
        renderTaskEditForm();

        expect(screen.getByLabelText("Title")).toHaveValue(
            "Existing task"
        );

        expect(screen.getByLabelText("Description")).toHaveValue(
            "Existing description"
        );

        expect(screen.getByLabelText("Status")).toHaveValue(
            "IN_PROGRESS"
        );

        expect(screen.getByLabelText("Priority")).toHaveValue(
            "HIGH"
        );

        expect(screen.getByLabelText("Due date")).toHaveValue(
            "2026-09-30"
        );
    });

    it("shows validation errors when submitted empty", async () => {
        const user = userEvent.setup();

        renderTaskEditForm();

        await user.clear(screen.getByLabelText("Title"));
        await user.clear(screen.getByLabelText("Description"));

        fireEvent.change(screen.getByLabelText("Due date"), {
            target: {
                value: "",
            },
        });

        await user.click(
            screen.getByRole("button", {
                name: "Save",
            })
        );

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

    it("calls onSave with the edited data, including status and priority", async () => {
        const user = userEvent.setup();
        const onSave = vi.fn().mockResolvedValue(undefined);

        renderTaskEditForm(onSave);

        await user.clear(screen.getByLabelText("Title"));
        await user.type(
            screen.getByLabelText("Title"),
            "Updated task"
        );

        await user.clear(screen.getByLabelText("Description"));
        await user.type(
            screen.getByLabelText("Description"),
            "Updated description"
        );

        await user.selectOptions(
            screen.getByLabelText("Status"),
            "COMPLETED"
        );

        await user.selectOptions(
            screen.getByLabelText("Priority"),
            "MEDIUM"
        );

        fireEvent.change(screen.getByLabelText("Due date"), {
            target: {
                value: "2026-10-15",
            },
        });

        await user.click(
            screen.getByRole("button", {
                name: "Save",
            })
        );

        await waitFor(() => {
            expect(onSave).toHaveBeenCalledWith({
                title: "Updated task",
                description: "Updated description",
                status: "COMPLETED",
                priority: "MEDIUM",
                dueDate: "2026-10-15",
            });
        });
    });

    it("calls onCancel when cancel is clicked", async () => {
        const user = userEvent.setup();
        const onCancel = vi.fn();

        renderTaskEditForm(
            vi.fn().mockResolvedValue(undefined),
            onCancel
        );

        await user.click(
            screen.getByRole("button", {
                name: "Cancel",
            })
        );

        expect(onCancel).toHaveBeenCalledTimes(1);
    });
});