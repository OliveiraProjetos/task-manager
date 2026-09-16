import { Link } from "react-router-dom";
import RegisterForm from "../components/RegisterForm";

type RegisterPageProps = {
    onRegistered: () => void;
};

function RegisterPage({ onRegistered }: RegisterPageProps) {
    return (
        <div className="auth-page">
            <div className="auth-container">
                <h1>Task Manager</h1>
                <p className="auth-subtitle">Create your account</p>

                <RegisterForm onRegistered={onRegistered} />

                <p className="auth-link">
                    Already have an account?{" "}
                    <Link to="/login">Login</Link>
                </p>
            </div>
        </div>
    );
}

export default RegisterPage;