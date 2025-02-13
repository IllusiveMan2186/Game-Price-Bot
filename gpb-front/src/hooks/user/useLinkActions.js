import { useNavigation } from "@contexts/NavigationContext";
import { useHttpHelper } from "@hooks/useHttpHelper"

// Centralized API endpoints
const API_ENDPOINTS = {
    ACTIVATE_USER: `/activate`,
    LINK_USER: `/linker`,
    TOKE_SET: `/linker/set`,
};

export const useLinkActions = () => {
    const navigate = useNavigation();
    const { handleRequest } = useHttpHelper();

    // Link user account
    const accountLinkRequest = (token, setErrorMessage) => {
        handleRequest(
            "POST",
            API_ENDPOINTS.LINK_USER,
            { token },
            () => { navigate("/"); }
        );
    };

    // Get linke token for user account link
    const getLinkTokenRequest = (setToken) => {
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
    const getLinkTokenForMessengerRequest = (messengeUrl) => {
        handleRequest(
            "GET",
            API_ENDPOINTS.LINK_USER,
            null,
            (response) => {
                if (messengeUrl) {
                    window.open(messengeUrl + response.data, '_blank');
                }
            }
        );
    };

    return { accountLinkRequest, getLinkTokenRequest, getLinkTokenForMessengerRequest };
};
