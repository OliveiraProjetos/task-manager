import * as React from "react";
import { Link } from "react-router-dom";

import type { Task, UpdateTaskRequest } from "../types/Task";
import TaskEditForm from "./TaskEditForm";
import { getUserErrorMessage, isUnauthorizedError } from "../utils/apiError";
import {useAuth} from "../contexts/AuthContext.tsx";

type TaskCardProps = {
    task: Task;
    onUpdate: (
        id: string,
        data: UpdateTaskRequest
    ) => Promise<void>;
    onDelete: (id: string) => Promise<void>;
};

function TaskCard({task, onUpdate, onDelete,}: TaskCardProps) {
    const [deleting, setDeleting] = React.useState(false);
    const [editing, setEditing] = React.useState(false);
    const [error, setError] = React.useState<string | null>(null);

    function handleEdit() {
        setError(null);
        setEditing(true);
    }

    function handleCancel() {
        setError(null);
        setEditing(false);
    }

    async function handleDelete() {
        const { logout } = useAuth();
        const confirmed = window.confirm(
            "Are you sure you want to delete this task?"
        );

        if (!confirmed) {
            return;
        }

        setError(null);
        setDeleting(true);

        try {
            await onDelete(task.id);
        } catch (error) {
            if (isUnauthorizedError(error)) {
                logout();
                return;
            }

            setError(getUserErrorMessage(error));
        } finally {
            setDeleting(false);
        }
    }

    if (editing) {
        return (
            <article className="task-card">
                <TaskEditForm
                    task={task}
                    onSave={async (data) => {
                        await onUpdate(task.id, {
                            ...data,
                            status: task.status,
                            priority: task.priority,
                        });

                        setEditing(false);
                    }}
                    onCancel={handleCancel}
                />
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
                <Link
                    className="secondary-button"
                    to={`/tasks/${task.id}`}
                >
                    View details
                </Link>

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