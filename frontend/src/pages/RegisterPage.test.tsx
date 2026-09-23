import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";
import RegisterPage from "./RegisterPage";
import { register } from "../services/authService";

vi.mock("../services/authService", () => ({
    register: vi.fn(),
}));

describe("RegisterPage", () => {
    it("renders the register form", () => {
        render(
            <MemoryRouter>
                <RegisterPage onRegistered={vi.fn()} />
            </MemoryRouter>
        );

        expect(
            screen.getByRole("heading", {
                name: "Register",
            })
        ).toBeInTheDocument();

        expect(
            screen.getByLabelText("Username")
        ).toBeInTheDocument();

        expect(
            screen.getByLabelText("Password")
        ).toBeInTheDocument();

        expect(
            screen.getByLabelText("Confirm password")
        ).toBeInTheDocument();
    });

    it("calls onRegistered after successful registration", async () => {
        const user = userEvent.setup();
        const onRegistered = vi.fn();

        vi.mocked(register).mockResolvedValue();

        render(
            <MemoryRouter>
                <RegisterPage onRegistered={onRegistered} />
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

        await user.type(
            screen.getByLabelText("Confirm password"),
            "password123"
        );

        await user.click(
            screen.getByRole("button", {
                name: /register/i,
            })
        );

        expect(register).toHaveBeenCalledWith({
            username: "testuser",
            password: "password123",
        });

        expect(onRegistered).toHaveBeenCalled();
    });
});