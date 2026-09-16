import { z } from "zod";

export const taskSchema = z.object({
    title: z
        .string()
        .trim()
        .min(1, "Title is required")
        .max(100, "Title must be at most 100 characters"),

    description: z
        .string()
        .trim()
        .min(1, "Description is required")
        .max(500, "Description must be at most 500 characters"),

    dueDate: z
        .string()
        .min(1, "Due date is required"),
});

export type TaskFormData = z.infer<typeof taskSchema>;