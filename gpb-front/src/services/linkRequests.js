import { handleRequest, handleError } from '@util/httpHelper';

// Centralized API Endpoints
const API_ENDPOINTS = {
    ACTIVATE_USER: `/activate`,
    LINK_USER: `/linker`,
    TOKE_SET: `/linker/set`,
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