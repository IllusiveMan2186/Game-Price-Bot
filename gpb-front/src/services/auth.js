import { changeLanguage } from 'i18next';
import { NotificationManager } from 'react-notifications';

import { setAuthHeader, setEmailHeader, setRoleHeader, setLocaleHeader, getLocale } from '@util/authService';
import { handleRequest } from '@util/httpHelper';


// Centralized endpoints
const API_ENDPOINTS = {
    LOGIN: "/login",
    REGISTER: "/registration"
};


// Login request function
export const loginRequest = (email, password, setErrorMessage, navigate) => {
    handleRequest(
        "POST",
        API_ENDPOINTS.LOGIN,
        { email, password },
        (response) => {
            const { token, email, authorities, locale } = response.data;
            setAuthHeader(token);
            setEmailHeader(email);
            setRoleHeader(authorities[0]?.authority || "user");
            setLocaleHeader(locale);
            changeLanguage(locale);
            navigate("/");
        },
        (errorMessage) => setErrorMessage(errorMessage)
    );
};

// Registration request function
export const registerRequest = (email, password, setErrorMessage, navigate) => {
    handleRequest(
        "POST",
        API_ENDPOINTS.REGISTER,
        { email, password, locale: getLocale() },
        () => {
            NotificationManager.success("Registration successful!", "Welcome");
            navigate("/");
        },
        (errorMessage) => setErrorMessage(errorMessage)
    );
};
