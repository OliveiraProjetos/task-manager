import * as React from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import { useAuth } from "../contexts/useAuth";
import {taskSchema, type TaskFormData,} from "../schemas/taskSchema";
import type { Task } from "../types/Task"
import { getUserErrorMessage, isUnauthorizedError } from "../utils/apiError";

type TaskEditFormProps = {
    task: Task;
    onSave: (data: TaskFormData) => Promise<void>;
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
    } = useForm<TaskFormData>({
        resolver: zodResolver(taskSchema),
        defaultValues: {
            title: task.title,
            description: task.description,
            dueDate: task.dueDate,
        },
    });

    async function handleSave(data: TaskFormData) {
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