import { createContext, useContext, useState, useEffect } from "react";

const AuthContext = createContext();

export function AuthProvider({ children }) {

    const getAccessToken = () => window.localStorage.getItem('AUTH_TOKEN');

    const setAccessToken = (token) => {
        window.localStorage.removeItem('LINK_TOKEN');
        if (token) {
            window.localStorage.setItem('AUTH_TOKEN', token);
        } else {
            window.localStorage.removeItem('AUTH_TOKEN', token);
        }
    };

    const getLinkToken = () => window.localStorage.getItem('LINK_TOKEN');

    const setLinkToken = (token) => {
        window.localStorage.setItem('LINK_TOKEN', token);
    };

    const getUserRole = () => window.localStorage.getItem('USER_ROLE');

    const setUserRole = (token) => {
        window.localStorage.setItem('USER_ROLE', token);
    };

    const logout = () => {
        setAccessToken(null);
        window.localStorage.removeItem('LINK_TOKEN');
        window.localStorage.removeItem('USER_ROLE');
    };

    const isUserAuth = () => {
        return !!getAccessToken();
    };

    return (
        <AuthContext.Provider value={{
            getAccessToken, setAccessToken,
            getLinkToken, setLinkToken,
            getUserRole, setUserRole,
            logout,
            isUserAdmin: () => getUserRole() === "ROLE_ADMIN",
            isUserAuth
        }}>
            {children}
        </AuthContext.Provider>
    );
}

// Hook to access authentication context
export function useAuth() {
    return useContext(AuthContext);
}
