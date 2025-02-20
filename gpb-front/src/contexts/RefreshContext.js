import { createContext, useEffect } from "react";
import { useNavigation } from "@contexts/NavigationContext";
import { useAuth } from "@contexts/AuthContext";
import { setupInterceptors, refreshToken } from "@services/httpService";

const RefreshContext = createContext(null);

export const RefreshProvider = ({ children }) => {
    const navigate = useNavigation();
    const { isUserAuth, setAccessToken, logout } = useAuth();

    // Refresh token when the app loads
    useEffect(() => {
        setupInterceptors(setAccessToken, logout, navigate);
        if (!isUserAuth()) {
            refreshToken(setAccessToken)
        }
    }, []);

    return (
        <RefreshContext.Provider value={{}}>
            {children}
        </RefreshContext.Provider>
    );
};
