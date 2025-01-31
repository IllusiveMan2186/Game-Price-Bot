import { handleRequest, handleError } from '@util/httpHelper';
import { setAuthToken, setAuthFlag } from '@util/authUtils';
import {  setEmail } from '@util/userDataUtils';

// Centralized API Endpoints
const API_ENDPOINTS = {
    RESEND_EMAIL: `/user/resend/email`,
    CHANGE_EMAIL: `/user/email`,
    CHANGE_PASSWORD: '/user/password',
    CHANGE_LOCALE: `/user/locale`,
    CHECK_AUTH: `/check-auth`,
};

// Activate user account
export const activateUserAccountRequest = (token, navigate) => {
    handleRequest(
        "POST",
        API_ENDPOINTS.ACTIVATE_USER,
        { token },
        () => navigate("/login"),
        () => console.error("Failed to activate user")
    );
};

// Resend Activation Email
export const resendActivationEmailRequest = (email, navigate) => {
    handleRequest(
        "POST",
        API_ENDPOINTS.RESEND_EMAIL,
        { email },
        () => navigate("/login"),
        (error) => console.error("Failed to resend activation email")
    );
};

// Change Email
export const emailChangeRequest = (event, email, setErrorMessage, navigate) => {
    handleRequest(
        "PUT",
        API_ENDPOINTS.CHANGE_EMAIL,
        { email },
        (response) => {
            setEmail(response.data.email);
            setAuthToken(response.data.token);
            navigate("/");
        },
        (error) => handleError(error, navigate, setErrorMessage)
    );
};

// Change Password
export const passwordChangeRequest = (event, password, setErrorMessage, navigate) => {
    handleRequest(
        "PUT",
        API_ENDPOINTS.CHANGE_PASSWORD,
        { password },
        () => navigate("/"),
        (error) => handleError(error, navigate, setErrorMessage)
    );
};

// Change Locale
export const localeChangeRequest = (locale) => {
    handleRequest(
        "PUT",
        API_ENDPOINTS.CHANGE_LOCALE,
        { locale },
        () => console.log("Locale changed successfully"),
        (error) => console.error("Failed to change locale", error)
    );
};

// Get linke token for user account link
export const checkAuthRequest = () => {
    return new Promise((resolve) => {
        handleRequest(
            "GET",
            API_ENDPOINTS.CHECK_AUTH,
            null,
            (response) => {
                if (response && response.status === 200) {
                    setAuthFlag();
                    resolve(true);
                } else {
                    window.localStorage.removeItem("IS_AUTHENTICATED");
                    resolve(false);
                }
            },
            () => {
                console.error("Failed to check auth");
                window.localStorage.removeItem("IS_AUTHENTICATED");
                resolve(false);
            }
        );
    });
};
