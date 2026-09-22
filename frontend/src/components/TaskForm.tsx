import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import { useAuth } from "../contexts/AuthContext";
import type { Task } from "../types/Task";
import { createTask } from "../services/taskService";
import {taskSchema, type TaskFormData,} from "../schemas/taskSchema";
import { getUserErrorMessage, isUnauthorizedError } from "../utils/apiError";

type TaskFormProps = {
    token: string;
    onTaskCreated: (task: Task) => void;
};

function TaskForm({ token, onTaskCreated }: TaskFormProps) {
    const { logout } = useAuth();
    const [error, setError] = useState<string | null>(null);

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

        try {
            const createdTask = await createTask(data, token);
            onTaskCreated(createdTask);
            reset();
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