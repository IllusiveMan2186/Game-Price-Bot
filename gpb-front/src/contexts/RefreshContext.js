import { createContext, useEffect } from "react";
import { useAuth } from "@contexts/AuthContext";
import { useNavigation } from "@contexts/NavigationContext";
import { registerAuthHandlers } from "@services/httpService";

export const RefreshContext = createContext(null);

export const RefreshProvider = ({ children }) => {
    const {
        setAccessToken,
        getAccessToken,
        getLinkToken,
        logout,
    } = useAuth();
    const navigate = useNavigation();

    useEffect(() => {
        registerAuthHandlers({ getAccessToken, setAccessToken, getLinkToken, logout, navigate });
    }, []);

    return (
        <RefreshContext.Provider value={{}}>
            {children}
        </RefreshContext.Provider>
    );
};
