export type Task = {
    id: string;
    title: string;
    description: string;
    status: "PENDING" | "IN_PROGRESS" | "COMPLETED";
    priority: "LOW" | "MEDIUM" | "HIGH";
    dueDate: string;
};

export type CreateTaskRequest = {
    title: string;
    description: string;
    dueDate: string;
};

export type UpdateTaskRequest = {
    title: string;
    description: string;
    status: Task["status"];
    priority: Task["priority"];
    dueDate: string;
};