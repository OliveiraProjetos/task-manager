import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import type { Task } from "../types/Task";
import { createTask } from "../services/taskService";
import {
    taskSchema,
    type TaskFormData,
} from "../schemas/taskSchema";

type TaskFormProps = {
    token: string;
    onTaskCreated: (task: Task) => void;
};

function TaskForm({ token, onTaskCreated }: TaskFormProps) {
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);

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
            title: "",
            description: "",
            dueDate: "",
        },
    });

    async function onSubmit(data: TaskFormData) {
        setError(null);
        setSuccess(false);

        try {
            const createdTask = await createTask(
                data,
                token
            );

            onTaskCreated(createdTask);
            reset();
            setSuccess(true);
        } catch (error) {
            if (
                error instanceof Error &&
                error.message === "UNAUTHORIZED"
            ) {
                setError("Your session has expired");
                return;
            }

            setError("Failed to create task");
        }
    }

    return (
        <form
            className="form-card"
            onSubmit={handleSubmit(onSubmit)}
        >
            <div className="form-group">
                <label htmlFor="title">Title</label>

                <input
                    id="title"
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
                <label htmlFor="description">
                    Description
                </label>

                <textarea
                    id="description"
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
                <label htmlFor="dueDate">
                    Due date
                </label>

                <input
                    id="dueDate"
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

            {success && (
                <p className="form-success">
                    Task created successfully.
                </p>
            )}

            <button
                className="primary-button"
                type="submit"
                disabled={isSubmitting}
            >
                {isSubmitting ? "Creating..." : "Add task"}
            </button>
        </form>
    );
}

export default TaskForm;