
import { useCallback, useState, type ReactNode } from "react";
import { AuthContext } from "./authContext";

type AuthProviderProps = {
    children: ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
    const [token, setToken] = useState<string | null>(
        () => localStorage.getItem("token")
    );

    const isAuthenticated = token !== null;

    const login = useCallback((newToken: string) => {
        localStorage.setItem("token", newToken);
        setToken(newToken);
    }, []);

    const logout = useCallback(() => {
        localStorage.removeItem("token");
        setToken(null);
    }, []);

    return (
        <AuthContext.Provider
            value={{
                token,
                isAuthenticated,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}
