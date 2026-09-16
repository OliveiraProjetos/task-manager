import * as React from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import type { Task } from "../types/Task";
import {
    taskSchema,
    type TaskFormData,
} from "../schemas/taskSchema";

type TaskCardProps = {
    task: Task;
    onUpdate: (
        id: string,
        data: TaskFormData
    ) => Promise<void>;
    onDelete: (id: string) => Promise<void>;
};

function TaskCard({
                      task,
                      onUpdate,
                      onDelete,
                  }: TaskCardProps) {
    const [deleting, setDeleting] = React.useState(false);
    const [editing, setEditing] = React.useState(false);
    const [error, setError] = React.useState<string | null>(null);

    const {
        register,
        handleSubmit,
        reset,
        formState: {
            errors,
            isSubmitting,
        },
    } = useForm<TaskFormData>({
        resolver: zodResolver(taskSchema),
        defaultValues: {
            title: task.title,
            description: task.description,
            dueDate: task.dueDate,
        },
    });

    function handleEdit() {
        reset({
            title: task.title,
            description: task.description,
            dueDate: task.dueDate,
        });

        setError(null);
        setEditing(true);
    }

    function handleCancel() {
        reset({
            title: task.title,
            description: task.description,
            dueDate: task.dueDate,
        });

        setError(null);
        setEditing(false);
    }

    async function handleUpdate(data: TaskFormData) {
        setError(null);

        try {
            await onUpdate(task.id, data);
            setEditing(false);
        } catch {
            setError("Failed to update task");
        }
    }

    async function handleDelete() {
        setError(null);
        setDeleting(true);

        try {
            await onDelete(task.id);
        } catch {
            setError("Failed to delete task");
        } finally {
            setDeleting(false);
        }
    }

    if (editing) {
        return (
            <article className="task-card">
                <form
                    className="form-card"
                    onSubmit={handleSubmit(handleUpdate)}
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

                    {error && <p className="form-error">{error}</p>}

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
                            onClick={handleCancel}
                            disabled={isSubmitting}
                        >
                            Cancel
                        </button>
                    </div>
                </form>
            </article>
        );
    }

    return (
        <article className="task-card">
            <h3>{task.title}</h3>

            <p>{task.description}</p>

            <div className="task-meta">
                <span className={`badge status-${task.status.toLowerCase()}`}>
                    Status: {task.status}
                </span>

                <span className={`badge priority-${task.priority.toLowerCase()}`}>
                    Priority: {task.priority}
                </span>

                <span className="badge">
                    Due: {task.dueDate}
                </span>
            </div>

            {error && <p className="form-error">{error}</p>}

            <div className="task-actions">
                <button
                    className="secondary-button"
                    onClick={handleEdit}
                    disabled={deleting}
                >
                    Edit
                </button>

                <button
                    className="danger-button"
                    onClick={handleDelete}
                    disabled={deleting}
                >
                    {deleting ? "Deleting..." : "Delete"}
                </button>
            </div>
        </article>
    );
}

export default TaskCard;