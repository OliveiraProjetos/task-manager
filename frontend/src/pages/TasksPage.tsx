import { useEffect, useState } from "react";
import Header from "../components/Header";
import TaskForm from "../components/TaskForm";
import TaskList from "../components/TaskList";
import { useAuth } from "../contexts/useAuth";
import {deleteTask, getTasks, updateTask } from "../services/taskService";
import type { Task, UpdateTaskRequest } from "../types/Task";
import { getUserErrorMessage, isUnauthorizedError } from "../utils/apiError";

function TasksPage() {
    const [tasks, setTasks] = useState<Task[]>([]);
    const [selectedPriority, setSelectedPriority] = useState<Task["priority"] | "ALL">("ALL");
    const [selectedStatus, setSelectedStatus] = useState<Task["status"] | "ALL">("ALL");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);
    const { token, logout } = useAuth();
    const filteredTasks = tasks.filter(task => {
        const matchesStatus =
            selectedStatus === "ALL" || task.status === selectedStatus;

        const matchesPriority =
            selectedPriority === "ALL" || task.priority === selectedPriority;

        return matchesStatus && matchesPriority;
    });

    useEffect(() => {

        async function loadTasks() {
            setLoading(true);
            setError(null);

            try {
                const data = await getTasks(token!);
                setTasks(data);
            } catch (error) {
                if (isUnauthorizedError(error)) {
                    logout();
                    return;
                }

                setError(getUserErrorMessage(error));
            } finally {
                setLoading(false);
            }
        }

        void loadTasks();
    }, [token, logout]);

    function handleTaskCreated(task: Task) {
        setTasks(currentTasks => [...currentTasks, task]);
        setSuccessMessage("Task created successfully.");
    }

    async function handleDeleteTask(id: string) {
        setSuccessMessage(null);

        try {
            await deleteTask(id, token!);

            setTasks(currentTasks =>
                currentTasks.filter(task => task.id !== id)
            );

            setSuccessMessage("Task deleted successfully.");
        } catch (error) {
            if (isUnauthorizedError(error)) {
                logout();
                return;
            }

            setError(getUserErrorMessage(error));
        }
    }

    async function handleUpdateTask(id: string, data: UpdateTaskRequest) {
        setSuccessMessage(null);

        try {
            const updatedTask = await updateTask(
                id,
                data,
                token!
            );

            setTasks(currentTasks =>
                currentTasks.map(task =>
                    task.id === id ? updatedTask : task
                )
            );

            setSuccessMessage("Task updated successfully.");
        } catch (error) {
            if (isUnauthorizedError(error)) {
                logout();
                return;
            }

            setError(getUserErrorMessage(error));
        }
    }

    return (
        <div className="app">
            <main className="page-container">
                <Header
                    title="Task Manager"
                    subtitle="Manage your tasks"
                />

                <div className="button-group">
                    <button
                        className="secondary-button"
                        onClick={logout}
                    >
                        Logout
                    </button>
                </div>

                {successMessage && (
                    <p className="form-success">
                        {successMessage}
                    </p>
                )}

                <section className="content-section">
                    <TaskForm
                        token={token!}
                        onTaskCreated={handleTaskCreated}
                    />
                </section>

                <section className="content-section">
                    {loading ? (
                        <div className="empty-state">
                            <p>Loading tasks...</p>
                        </div>
                    ) : error ? (
                        <div className="error-state">
                            <p>{error}</p>
                        </div>
                    ) : tasks.length === 0 ? (
                        <div className="empty-state">
                            <h3>No tasks yet</h3>
                            <p>Create your first task using the form above.</p>
                        </div>
                    ) : (
                        <>
                            <div className="form-group">
                                <label htmlFor="status-filter">
                                    Filter by status
                                </label>

                                <select
                                    id="status-filter"
                                    value={selectedStatus}
                                    onChange={event =>
                                        setSelectedStatus(
                                            event.target.value as Task["status"] | "ALL"
                                        )
                                    }
                                >
                                    <option value="ALL">All</option>
                                    <option value="PENDING">Pending</option>
                                    <option value="IN_PROGRESS">In Progress</option>
                                    <option value="COMPLETED">Completed</option>
                                </select>
                            </div>

                            <div className="form-group">
                                <label htmlFor="priority-filter">
                                    Filter by priority
                                </label>

                                <select
                                    id="priority-filter"
                                    value={selectedPriority}
                                    onChange={event =>
                                        setSelectedPriority(
                                            event.target.value as Task["priority"] | "ALL"
                                        )
                                    }
                                >
                                    <option value="ALL">All</option>
                                    <option value="LOW">Low</option>
                                    <option value="MEDIUM">Medium</option>
                                    <option value="HIGH">High</option>
                                </select>
                            </div>

                            {filteredTasks.length === 0 ? (
                                <div className="empty-state">
                                    <h3>No tasks match this filter</h3>
                                    <p>Try selecting a different status.</p>
                                </div>
                            ) : (
                                <TaskList
                                    tasks={filteredTasks}
                                    onUpdate={handleUpdateTask}
                                    onDelete={handleDeleteTask}
                                />
                            )}
                        </>
                    )}
                </section>
            </main>
        </div>
    );
}

export default TasksPage;