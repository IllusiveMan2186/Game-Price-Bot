import { createContext, useEffect, useState } from "react";
import { useAuth } from "@contexts/AuthContext";
import { setupInterceptors,refreshToken } from "@services/httpService";

const RefreshContext = createContext(null);

export const RefreshProvider = ({ children }) => {
    const [loading, setLoading] = useState(true);
    const { isUserAuth, setAccessToken, logout } = useAuth();

    // Refresh token when the app loads
    useEffect(() => {
        setupInterceptors(setAccessToken, logout);
    }, []);

    return (
        <RefreshContext.Provider value={{ refreshToken, loading }}>
            {children}
        </RefreshContext.Provider>
    );
};
