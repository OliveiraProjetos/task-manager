import * as React from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import { useAuth } from "../contexts/useAuth";
import {taskEditSchema, type TaskEditFormData,} from "../schemas/taskSchema";
import type { Task } from "../types/Task"
import { getUserErrorMessage, isUnauthorizedError } from "../utils/apiError";

type TaskEditFormProps = {
    task: Task;
    onSave: (data: TaskEditFormData) => Promise<void>;
    onCancel: () => void;
};

function TaskEditForm({task, onSave, onCancel,}: TaskEditFormProps) {
    const { logout } = useAuth();
    const [error, setError] = React.useState<string | null>(null);

    const {
        register,
        handleSubmit,
        formState: {
            errors,
            isSubmitting,
        },
    } = useForm<TaskEditFormData>({
        resolver: zodResolver(taskEditSchema),
        defaultValues: {
            title: task.title,
            description: task.description,
            status: task.status,
            priority: task.priority,
            dueDate: task.dueDate,
        },
    });

    async function handleSave(data: TaskEditFormData) {
        setError(null);

        try {
            await onSave(data);
        } catch (error) {
            if (isUnauthorizedError(error)) {
                logout();
                return;
            }

            setError(getUserErrorMessage(error));
        }
    }

    return (
        <form
            className="form-card"
            onSubmit={handleSubmit(handleSave)}
        >
            <div className="form-group">
                <label htmlFor={`title-${task.id}`}>
                    Title
                </label>

                <input
                    id={`title-${task.id}`}
                    type="text"
                    {...register("title")}
                    disabled={isSubmitting}
                />

                {errors.title && (
                    <p className="form-error">
                        {errors.title.message}
                    </p>
                )}
            </div>

            <div className="form-group">
                <label htmlFor={`description-${task.id}`}>
                    Description
                </label>

                <textarea
                    id={`description-${task.id}`}
                    {...register("description")}
                    disabled={isSubmitting}
                />

                {errors.description && (
                    <p className="form-error">
                        {errors.description.message}
                    </p>
                )}
            </div>

            <div className="form-group">
                <label htmlFor={`status-${task.id}`}>
                    Status
                </label>

                <select
                    id={`status-${task.id}`}
                    {...register("status")}
                    disabled={isSubmitting}
                >
                    <option value="PENDING">Pending</option>
                    <option value="IN_PROGRESS">In progress</option>
                    <option value="COMPLETED">Completed</option>
                </select>

                {errors.status && (
                    <p className="form-error">
                        {errors.status.message}
                    </p>
                )}
            </div>

            <div className="form-group">
                <label htmlFor={`priority-${task.id}`}>
                    Priority
                </label>

                <select
                    id={`priority-${task.id}`}
                    {...register("priority")}
                    disabled={isSubmitting}
                >
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                </select>

                {errors.priority && (
                    <p className="form-error">
                        {errors.priority.message}
                    </p>
                )}
            </div>

            <div className="form-group">
                <label htmlFor={`dueDate-${task.id}`}>
                    Due date
                </label>

                <input
                    id={`dueDate-${task.id}`}
                    type="date"
                    {...register("dueDate")}
                    disabled={isSubmitting}
                />

                {errors.dueDate && (
                    <p className="form-error">
                        {errors.dueDate.message}
                    </p>
                )}
            </div>

            {error && (
                <p className="form-error">
                    {error}
                </p>
            )}

            <div className="button-group">
                <button
                    className="primary-button"
                    type="submit"
                    disabled={isSubmitting}
                >
                    {isSubmitting ? "Saving..." : "Save"}
                </button>

                <button
                    className="secondary-button"
                    type="button"
                    onClick={onCancel}
                    disabled={isSubmitting}
                >
                    Cancel
                </button>
            </div>
        </form>
    );
}

export default TaskEditForm;