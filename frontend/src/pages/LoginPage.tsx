import { Link } from "react-router-dom";
import LoginForm from "../components/LoginForm";

type LoginPageProps = {
    onLogin: (token: string) => void;
};

function LoginPage({ onLogin }: LoginPageProps) {
    return (
        <div className="auth-page">
            <div className="auth-container">
                <h1>Task Manager</h1>
                <p className="auth-subtitle">Sign in to manage your tasks</p>

                <LoginForm onLogin={onLogin} />

                <p className="auth-link">
                    Don't have an account?{" "}
                    <Link to="/register">Register</Link>
                </p>
            </div>
        </div>
    );
}

export default LoginPage;