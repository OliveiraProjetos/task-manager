import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import RegisterForm from "./RegisterForm";
import { register } from "../services/authService";

vi.mock("../services/authService", () => ({
    register: vi.fn(),
}));

describe("RegisterForm", () => {
    it("shows validation errors when submitted empty", async () => {
        const user = userEvent.setup();

        render(
            <RegisterForm
                onRegistered={vi.fn()}
            />
        );

        await user.click(
            screen.getByRole("button", {
                name: /register/i,
            })
        );

        expect(
            screen.getByText("Username is required")
        ).toBeInTheDocument();

        expect(
            screen.getByText("Password must be at least 8 characters")
        ).toBeInTheDocument();

        expect(
            screen.getByText("Please confirm your password")
        ).toBeInTheDocument();
    });

    it("registers a user with valid data", async () => {
        const user = userEvent.setup();
        const onRegistered = vi.fn();

        vi.mocked(register).mockResolvedValue();

        render(
            <RegisterForm
                onRegistered={onRegistered}
            />
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