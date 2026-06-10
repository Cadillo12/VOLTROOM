import { createContext, useEffect, useState } from "react";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [token, setToken] = useState(localStorage.getItem("token"));
    const [userEmail, setUserEmail] = useState(localStorage.getItem("userEmail"));
    const [roles, setRoles] = useState(JSON.parse(localStorage.getItem("roles") || "[]"));

    useEffect(() => {
        if (token) {
            localStorage.setItem("token", token);
        } else {
            localStorage.removeItem("token");
        }
    }, [token]);

    const login = (authResponse) => {
        setToken(authResponse.token);
        setUserEmail(authResponse.email);
        setRoles(authResponse.roles || []);

        localStorage.setItem("token", authResponse.token);
        localStorage.setItem("userEmail", authResponse.email || "");
        localStorage.setItem("roles", JSON.stringify(authResponse.roles || []));
    };

    const logout = () => {
        setToken(null);
        setUserEmail(null);
        setRoles([]);
        localStorage.removeItem("token");
        localStorage.removeItem("userEmail");
        localStorage.removeItem("roles");
    };

    return (
        <AuthContext.Provider value={{ token, userEmail, roles, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}