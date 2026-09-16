import { useEffect, useState } from "react";
import Header from "../components/Header";
import TaskForm from "../components/TaskForm";
import TaskList from "../components/TaskList";
import { useAuth } from "../contexts/AuthContext";
import {deleteTask, getTasks, updateTask } from "../services/taskService";
import type { Task } from "../types/Task";

function TasksPage() {
    const [tasks, setTasks] = useState<Task[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const { token, logout } = useAuth();

    useEffect(() => {

        async function loadTasks() {
            setLoading(true);
            setError(null);

            try {
                const data = await getTasks(token!);
                setTasks(data);
            } catch (error) {
                if (error instanceof Error && error.message === "UNAUTHORIZED") {
                    logout();
                    return;
                }

                setError("Failed to load tasks");
            } finally {
                setLoading(false);
            }
        }

        void loadTasks();
    }, [token]);

    function handleTaskCreated(task: Task) {
        setTasks(currentTasks => [...currentTasks, task]);
    }

    async function handleDeleteTask(id: string) {
        try {
            await deleteTask(id, token!);

            setTasks(currentTasks =>
                currentTasks.filter(task => task.id !== id)
            );
        } catch (error) {
            if (
                error instanceof Error &&
                error.message === "UNAUTHORIZED"
            ) {
                logout();
                return;
            }

            throw error;
        }
    }

    async function handleUpdateTask(
        id: string,
        data: {
            title: string;
            description: string;
            dueDate: string;
        }
    ) {
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
        } catch (error) {
            if (
                error instanceof Error &&
                error.message === "UNAUTHORIZED"
            ) {
                logout();
                return;
            }

            throw error;
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
                        <TaskList
                            tasks={tasks}
                            onUpdate={handleUpdateTask}
                            onDelete={handleDeleteTask}
                        />
                    )}
                </section>
            </main>
        </div>
    );
}

export default TasksPage;