import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";
import LoginPage from "./LoginPage";
import { AuthProvider } from "../contexts/AuthContext";
import { login } from "../services/authService";

vi.mock("../services/authService", () => ({
    login: vi.fn(),
}));

describe("LoginPage", () => {
    it("renders the login form", () => {
        render(
            <MemoryRouter>
                <AuthProvider>
                    <LoginPage onLogin={vi.fn()} />
                </AuthProvider>
            </MemoryRouter>
        );

        expect(
            screen.getByRole("heading", {
                name: "Login",
            })
        ).toBeInTheDocument();

        expect(
            screen.getByLabelText("Username")
        ).toBeInTheDocument();

        expect(
            screen.getByLabelText("Password")
        ).toBeInTheDocument();
    });

    it("calls onLogin after successful login", async () => {
        const user = userEvent.setup();
        const onLogin = vi.fn();

        vi.mocked(login).mockResolvedValue({
            token: "test-token",
        });

        render(
            <MemoryRouter>
                <AuthProvider>
                    <LoginPage onLogin={onLogin} />
                </AuthProvider>
            </MemoryRouter>
        );

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

        expect(onLogin).toHaveBeenCalledWith("test-token");
    });
});