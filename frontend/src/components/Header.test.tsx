import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import Header from "./Header";

describe("Header", () => {
    it("renders the title and subtitle", () => {
        render(
            <Header
                title="Task Manager"
                subtitle="Manage your tasks"
            />
        );

        expect(
            screen.getByRole("heading", {
                name: "Task Manager",
            })
        ).toBeInTheDocument();

        expect(
            screen.getByText("Manage your tasks")
        ).toBeInTheDocument();
    });
});