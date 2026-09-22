import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import Header from "../components/Header";
import TaskEditForm from "../components/TaskEditForm";
import { useAuth } from "../contexts/AuthContext";
import { getTask, updateTask } from "../services/taskService";
import { isUnauthorizedError } from "../utils/apiError";
import type { Task, UpdateTaskRequest } from "../types/Task";

function TaskDetailsPage() {
    const { id } = useParams<{ id: string }>();
    const { token, logout } = useAuth();

    const [task, setTask] = useState<Task | null>(null);
    const [loading, setLoading] = useState(true);
    const [editing, setEditing] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        async function loadTask() {
            if (!id) {
                setError("Task not found");
                setLoading(false);
                return;
            }

            setLoading(true);
            setError(null);

            try {
                const data = await getTask(id, token!);
                setTask(data);
            } catch (error) {
                if (isUnauthorizedError(error)) {
                    logout();
                    return;
                }

                if (
                    error instanceof Error &&
                    error.message === "TASK_NOT_FOUND"
                ) {
                    setError("Task not found");
                    return;
                }

                setError("Failed to load task");
            } finally {
                setLoading(false);
            }
        }

        void loadTask();
    }, [id, token]);

    async function handleUpdateTask(data: UpdateTaskRequest) {
        if (!task) {
            return;
        }

        try {
            const updatedTask = await updateTask(
                task.id,
                data,
                token!
            );

            setTask(updatedTask);
            setEditing(false);
            setError(null);
        } catch (error) {
            if (isUnauthorizedError(error)) {
                logout();
                return;
            }

            throw error;
        }
    }

    function handleCancelEdit() {
        setEditing(false);
        setError(null);
    }

    return (
        <div className="app">
            <main className="page-container">
                <Header
                    title="Task details"
                    subtitle="View your task information"
                />

                <div className="button-group">
                    <Link
                        className="secondary-button"
                        to="/tasks"
                    >
                        Back to tasks
                    </Link>
                </div>

                <section className="content-section">
                    {loading ? (
                        <div className="empty-state">
                            <p>Loading task...</p>
                        </div>
                    ) : error && !task ? (
                        <div className="error-state">
                            <p>{error}</p>
                        </div>
                    ) : task ? (
                        editing ? (
                            <article className="task-card">
                                <TaskEditForm
                                    task={task}
                                    onSave={async (data) => {
                                        await handleUpdateTask({
                                            ...data,
                                            status: task.status,
                                            priority: task.priority,
                                        });
                                    }}
                                    onCancel={handleCancelEdit}
                                />
                            </article>
                        ) : (
                            <article className="task-card">
                                <h2>{task.title}</h2>

                                <p>{task.description}</p>

                                <div className="task-meta">
                                    <span
                                        className={`badge status-${task.status.toLowerCase()}`}
                                    >
                                        Status: {task.status}
                                    </span>

                                    <span
                                        className={`badge priority-${task.priority.toLowerCase()}`}
                                    >
                                        Priority: {task.priority}
                                    </span>

                                    <span className="badge">
                                        Due: {task.dueDate}
                                    </span>
                                </div>

                                <div className="task-actions">
                                    <button
                                        className="secondary-button"
                                        onClick={() => setEditing(true)}
                                    >
                                        Edit
                                    </button>
                                </div>
                            </article>
                        )
                    ) : null}
                </section>
            </main>
        </div>
    );
}

export default TaskDetailsPage;