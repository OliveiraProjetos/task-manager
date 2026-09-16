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