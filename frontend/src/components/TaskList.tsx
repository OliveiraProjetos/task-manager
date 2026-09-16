import TaskCard from "./TaskCard";
import type { Task } from "../types/Task";
import type {TaskFormData} from "../schemas/taskSchema.ts";

type TaskListProps = {
    tasks: Task[];
    onUpdate: (
        id: string,
        data: TaskFormData
    ) => Promise<void>;
    onDelete: (id: string) => Promise<void>;
};

function TaskList({ tasks, onUpdate, onDelete }: TaskListProps) {
    return (
        <section className="content-section">
            <h2>My tasks</h2>

            <div className="tasks-grid">
                {tasks.map((task) => (
                    <TaskCard
                        key={task.id}
                        task={task}
                        onUpdate={onUpdate}
                        onDelete={onDelete}
                    />
                ))}
            </div>
        </section>
    );
}

export default TaskList;