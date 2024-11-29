import { handleRequest } from '../util/httpHelper';
import { setAuthHeader, setEmailHeader, defaultRequestErrorCheck } from '../util/authService';

// Centralized API Endpoints
const API_ENDPOINTS = {
    RESEND_EMAIL: `/user/resend/email`,
    CHANGE_EMAIL: `/user/email`,
    CHANGE_PASSWORD: '/user/password',
    CHANGE_LOCALE: `/user/locale`,
};

// Centralized Error Handler
const handleError = (error, navigate, setErrorMessage) => {
    defaultRequestErrorCheck(error);
    if (error?.response?.status === 401) {
        navigate("/login");
    }
    setErrorMessage?.(error?.response?.data || "An unexpected error occurred.");
};

// Resend Activation Email
export const resendActivationEmailRequest = (event, email, navigate) => {
    event.preventDefault();
    handleRequest(
        "POST",
        API_ENDPOINTS.RESEND_EMAIL,
        { email },
        () => navigate("/login"),
        () => console.error("Failed to resend activation email")
    );
};

// Change Email
export const emailChangeRequest = (event, email, setErrorMessage, navigate) => {
    event.preventDefault();
    handleRequest(
        "PUT",
        API_ENDPOINTS.CHANGE_EMAIL,
        { email },
        (response) => {
            setEmailHeader(response.data.email);
            setAuthHeader(response.data.token);
            navigate("/");
        },
        (error) => handleError(error, navigate, setErrorMessage)
    );
};

// Change Password
export const passwordChangeRequest = (event, password, setErrorMessage, navigate) => {
    event.preventDefault();
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
        () => console.info("Locale changed successfully"),
        (error) => console.error("Failed to change locale", error)
    );
};
