import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import LoginForm from "./LoginForm";
import { login } from "../services/authService";

vi.mock("../services/authService", () => ({
    login: vi.fn(),
}));

describe("LoginForm", () => {
    it("shows validation errors when submitted empty", async () => {
        const user = userEvent.setup();

        render(<LoginForm onLogin={vi.fn()} />);

        await user.click(
            screen.getByRole("button", {
                name: /login/i,
            })
        );

        expect(
            screen.getByText("Username is required")
        ).toBeInTheDocument();

        expect(
            screen.getByText("Password is required")
        ).toBeInTheDocument();
    });

    it("logs in with valid credentials", async () => {
        const user = userEvent.setup();
        const onLogin = vi.fn();

        vi.mocked(login).mockResolvedValue({
            token: "test-token",
        });

        render(<LoginForm onLogin={onLogin} />);

        await user.type(
            screen.getByLabelText("Username"),
            "testuser"
        );

        await user.type(
            screen.getByLabelText("Password"),
            "password123"
        );

        await user.click(
            screen.getByRole("button", {
                name: /login/i,
            })
        );

        await waitFor(() => {
            expect(login).toHaveBeenCalledWith({
                username: "testuser",
                password: "password123",
            });
        });

        expect(onLogin).toHaveBeenCalledWith("test-token");
    });
});