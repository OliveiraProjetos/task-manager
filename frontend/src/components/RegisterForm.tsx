import * as React from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import { register as registerUser } from "../services/authService";
import {
    registerSchema,
    type RegisterFormData,
} from "../schemas/authSchema";

type RegisterFormProps = {
    onRegistered: () => void;
};

function RegisterForm({ onRegistered }: RegisterFormProps) {
    const [error, setError] = React.useState<string | null>(null);

    const {
        register,
        handleSubmit,
        formState: {
            errors,
            isSubmitting,
        },
    } = useForm<RegisterFormData>({
        resolver: zodResolver(registerSchema),
        defaultValues: {
            username: "",
            password: "",
            confirmPassword: "",
        },
    });

    async function onSubmit(data: RegisterFormData) {
        setError(null);

        try {
            await registerUser({
                username: data.username,
                password: data.password,
            });

            onRegistered();
        } catch {
            setError("Failed to register");
        }
    }

    return (
        <form
            className="form-card"
            onSubmit={handleSubmit(onSubmit)}
        >
            <h2>Register</h2>

            <div className="form-group">
                <label htmlFor="username">Username</label>

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
                <label htmlFor="password">Password</label>

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

            <div className="form-group">
                <label htmlFor="confirmPassword">
                    Confirm password
                </label>

                <input
                    id="confirmPassword"
                    type="password"
                    {...register("confirmPassword")}
                    disabled={isSubmitting}
                />

                {errors.confirmPassword && (
                    <p className="form-error">
                        {errors.confirmPassword.message}
                    </p>
                )}
            </div>

            {error && <p className="form-error">{error}</p>}

            <button
                className="primary-button"
                type="submit"
                disabled={isSubmitting}
            >
                {isSubmitting ? "Creating..." : "Register"}
            </button>
        </form>
    );
}

export default RegisterForm;