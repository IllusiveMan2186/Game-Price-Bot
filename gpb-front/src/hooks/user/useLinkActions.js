import { useNavigation } from "@contexts/NavigationContext";
import { useHttpHelper } from "@hooks/useHttpHelper"

import { NotificationManager } from 'react-notifications';

import Message from '@util/message';

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
    const accountLinkRequest = (token) => {
        handleRequest(
            "POST",
            API_ENDPOINTS.LINK_USER,
            { token },
            () => {
                NotificationManager.success(<Message string={'app.account.link.success.message'} />, <Message string={'app.request.success.title'} />);

                navigate("/");
            }
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
