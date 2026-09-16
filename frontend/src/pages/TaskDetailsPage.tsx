import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import { getTask } from "../services/taskService";
import type { Task } from "../types/Task";

function TaskDetailsPage() {
    const { id } = useParams<{ id: string }>();

    const [task, setTask] = useState<Task | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const { token, logout } = useAuth();

    useEffect(() => {
        if (!id) {
            setError("Task not found");
            setLoading(false);
            return;
        }

        const taskId = id;

        async function loadTask() {
            try {
                setLoading(true);
                setError(null);

                const data = await getTask(taskId, token!);
                setTask(data);
            } catch (error) {
                if (
                    error instanceof Error &&
                    error.message === "UNAUTHORIZED"
                ) {
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
    }, [id, token, logout]);

    if (loading) {
        return <p>Loading task...</p>;
    }

    if (error) {
        return (
            <main>
                <p>{error}</p>
                <Link to="/tasks">Back to tasks</Link>
            </main>
        );
    }

    if (!task) {
        return null;
    }

    return (
        <>
            <header>
                <h1>Task details</h1>
                <button onClick={logout}>Logout</button>
            </header>

            <main>
                <h2>{task.title}</h2>

                <p>{task.description}</p>

                <p>Status: {task.status}</p>
                <p>Priority: {task.priority}</p>
                <p>Due date: {task.dueDate}</p>

                <Link to="/tasks">Back to tasks</Link>
            </main>
        </>
    );
}

export default TaskDetailsPage;