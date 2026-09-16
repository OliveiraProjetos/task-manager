import "./App.css"
import { Navigate, Route, Routes } from "react-router-dom";
import { useAuth } from "./contexts/AuthContext";

import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import TasksPage from "./pages/TasksPage";
import TaskDetailsPage from "./pages/TaskDetailsPage";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
    const { isAuthenticated, login } = useAuth();

    return (
        <Routes>
            <Route
                path="/login"
                element={
                    isAuthenticated ? (
                        <Navigate to="/tasks" replace />
                    ) : (
                        <LoginPage onLogin={login} />
                    )
                }
            />

            <Route
                path="/register"
                element={
                    isAuthenticated ? (
                        <Navigate to="/tasks" replace />
                    ) : (
                        <RegisterPage
                            onRegistered={() => {
                                window.location.href = "/login";
                            }}
                        />
                    )
                }
            />

            <Route
                path="/tasks"
                element={
                    <ProtectedRoute>
                        <TasksPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/tasks/:id"
                element={
                    <ProtectedRoute>
                        <TaskDetailsPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="*"
                element={
                    <Navigate
                        to={isAuthenticated ? "/tasks" : "/login"}
                        replace
                    />
                }
            />
        </Routes>
    );
}

export default App;