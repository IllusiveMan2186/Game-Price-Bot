import { handleRequest } from '@util/httpHelper';
import { setAuthHeader, setEmailHeader, defaultRequestErrorCheck } from '@util/authService';

// Centralized API Endpoints
const API_ENDPOINTS = {
    RESEND_EMAIL: `/user/resend/email`,
    CHANGE_EMAIL: `/user/email`,
    CHANGE_PASSWORD: '/user/password',
    CHANGE_LOCALE: `/user/locale`,
    ACTIVATE_USER: `/activate`,
    LINK_USER: `/linker`,
    TOKE_SET: `/linker/set`,
};

// Centralized Error Handler
const handleError = (error, navigate, setErrorMessage) => {
    defaultRequestErrorCheck(error);
    if (error?.response?.status === 401) {
        navigate("/login");
    }
    setErrorMessage?.(error?.response?.data || "An unexpected error occurred.");
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
            setEmailHeader(response.data.email);
            setAuthHeader(response.data.token);
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

// Link user account
export const accountLinkRequest = (token, setErrorMessage, navigate) => {
    handleRequest(
        "POST",
        API_ENDPOINTS.LINK_USER,
        { token },
        () => { navigate("/"); },
        (error) => handleError(error, navigate, setErrorMessage)
    );
};

// Get linke token for user account link
export const getLinkTokenRequest = (setToken) => {
    handleRequest(
        "GET",
        API_ENDPOINTS.LINK_USER,
        null,
        (response) => {
            setToken(response.data);
        },
        () => console.error("Failed to get link token")
    );
};

// Get linke token and redirect to messenger
export const getLinkTokenForMessengerRequest = (messengeUrl) => {
    handleRequest(
        "GET",
        API_ENDPOINTS.LINK_USER,
        null,
        (response) => {
            if (messengeUrl) {
                window.open(messengeUrl + response.data, '_blank');
            }
        },
        () => console.error("Failed to get link token")
    );
};