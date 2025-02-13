import { useNavigation } from "@contexts/NavigationContext";
import { useAuth } from "@contexts/AuthContext";
import { changeLanguage } from "i18next";
import { setLocale, getLocale } from "@util/userDataUtils";
import { useHttpHelper } from "@hooks/useHttpHelper"
import { logoutRequest } from "@services/httpService";

// Centralized API endpoints
const API_ENDPOINTS = {
    LOGIN: "/login",
    REGISTER: "/registration"
};

export const useAuthActions = () => {
    const navigate = useNavigation();
    const { setAccessToken, setUserRole, logout } = useAuth();
    const { handleRequest } = useHttpHelper();

    // Login request function
    const loginRequest = (email, password, setErrorMessage) => {
        handleRequest(
            "POST",
            API_ENDPOINTS.LOGIN,
            { email, password },
            (response) => {
                const { token, authorities, locale } = response.data;

                setAccessToken(token);
                setUserRole(authorities[0]?.authority || "user");
                setLocale(locale);
                changeLanguage(locale);
                navigate("/");
                window.location.reload();
            },
            (errorMessage) => setErrorMessage(errorMessage)
        );
    };

    // Registration request function
    const registerRequest = (email, password, setErrorMessage) => {
        handleRequest(
            "POST",
            API_ENDPOINTS.REGISTER,
            { email, password, locale: getLocale() },
            () => {
                navigate("/");
            },
            (errorMessage) => setErrorMessage(errorMessage)
        );
    };

    // Logout request function
    const userLogoutRequest = () => {
        return logoutRequest(logout);
    };

    return { loginRequest, registerRequest, userLogoutRequest };
};
