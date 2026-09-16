import * as React from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import { login } from "../services/authService";
import {
    loginSchema,
    type LoginFormData,
} from "../schemas/authSchema";

type LoginFormProps = {
    onLogin: (token: string) => void;
};

function LoginForm({ onLogin }: LoginFormProps) {
    const [error, setError] = React.useState<string | null>(null);

    const {
        register,
        handleSubmit,
        formState: {
            errors,
            isSubmitting,
        },
    } = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema),
        defaultValues: {
            username: "",
            password: "",
        },
    });

    async function onSubmit(data: LoginFormData) {
        setError(null);

        try {
            const response = await login(data);

            onLogin(response.token);
        } catch {
            setError("Invalid username or password");
        }
    }

    return (
        <form
            className="form-card"
            onSubmit={handleSubmit(onSubmit)}
        >
            <h2>Login</h2>

            <div className="form-group">
                <label htmlFor="username">
                    Username
                </label>

                <input
                    id="username"
                    type="text"
                    {...register("username")}
                    disabled={isSubmitting}
                />

                {errors.username && (
                    <p className="form-error">
                        {errors.username.message}
                    </p>
                )}
            </div>

            <div className="form-group">
                <label htmlFor="password">
                    Password
                </label>

                <input
                    id="password"
                    type="password"
                    {...register("password")}
                    disabled={isSubmitting}
                />

                {errors.password && (
                    <p className="form-error">
                        {errors.password.message}
                    </p>
                )}
            </div>

            {error && <p className="form-error">{error}</p>}

            <button
                className="primary-button"
                type="submit"
                disabled={isSubmitting}
            >
                {isSubmitting ? "Logging in..." : "Login"}
            </button>
        </form>
    );
}

export default LoginForm;