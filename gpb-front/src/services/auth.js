import { changeLanguage } from 'i18next';
import { NotificationManager } from 'react-notifications';

import { setAuthToken, setAuthFlag } from '@util/authUtils';
import { areCookiesEnabled } from '@util/cookieUtils';
import { setEmail, setUserRole, setLocale, getLocale } from '@util/userDataUtils';

import { handleRequest } from '@util/httpHelper';


// Centralized endpoints
const API_ENDPOINTS = {
    LOGIN: "/login",
    REGISTER: "/registration",
    LOGOUT: "/logout-user",
};


// Login request function
export const loginRequest = (email, password, setErrorMessage, navigate) => {
    const cookiesEnabled = areCookiesEnabled();

    handleRequest(
        "POST",
        API_ENDPOINTS.LOGIN,
        { email, password, cookiesEnabled },
        (response) => {
            const { token, email, authorities, locale } = response.data;

            if (!cookiesEnabled) {
                setAuthToken(token);
            }

            setEmail(email);
            setUserRole(authorities[0]?.authority || "user");
            setLocale(locale);
            changeLanguage(locale);
            setAuthFlag();
            navigate("/");
            window.location.reload();
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

// Login request function
export const logoutRequest = () => {
    handleRequest(
        "POST",
        API_ENDPOINTS.LOGOUT,
        null,
        () => console.log("User logout"),
        (error) => console.log("User logout error:" + error)
    );
};