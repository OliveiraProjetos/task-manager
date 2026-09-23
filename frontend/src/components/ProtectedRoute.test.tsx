import { render, screen } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import { describe, expect, it } from "vitest";
import ProtectedRoute from "./ProtectedRoute";
import { AuthProvider } from "../contexts/AuthContext";

function renderProtectedRoute(initialPath = "/protected") {
    return render(
        <MemoryRouter initialEntries={[initialPath]}>
            <AuthProvider>
                <Routes>
                    <Route
                        path="/protected"
                        element={
                            <ProtectedRoute>
                                <h1>Protected content</h1>
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/login"
                        element={<h1>Login page</h1>}
                    />
                </Routes>
            </AuthProvider>
        </MemoryRouter>
    );
}

describe("ProtectedRoute", () => {
    it("redirects unauthenticated users to login", () => {
        renderProtectedRoute();

        expect(
            screen.getByRole("heading", {
                name: "Login page",
            })
        ).toBeInTheDocument();

        expect(
            screen.queryByRole("heading", {
                name: "Protected content",
            })
        ).not.toBeInTheDocument();
    });

    it("renders protected content for authenticated users", () => {
        localStorage.setItem("token", "test-token");

        renderProtectedRoute();

        expect(
            screen.getByRole("heading", {
                name: "Protected content",
            })
        ).toBeInTheDocument();
    });
});