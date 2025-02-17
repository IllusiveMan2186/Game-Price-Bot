import { useNavigation } from "@contexts/NavigationContext";
import { useHttpHelper } from "@hooks/useHttpHelper";
import { NotificationManager } from 'react-notifications';

import Message from '@util/message';

const API_ENDPOINTS = {
    RESEND_EMAIL: `/user/resend/email`,
    CHANGE_EMAIL: `/email`,
    CHANGE_PASSWORD: '/user/password',
    CHANGE_LOCALE: `/user/locale`,
    CHECK_AUTH: `/check-auth`,
};

export const useUserActions = () => {
    const { handleRequest } = useHttpHelper();
    const navigate = useNavigation();

    // Change Email
    const emailChangeRequest = (email, setErrorMessage) => {
        handleRequest(
            "PUT",
            API_ENDPOINTS.CHANGE_EMAIL,
            { email },
            (response) => {
                NotificationManager.success(<Message string={'app.email.change.success.message'} />, <Message string={'app.request.success.title'} />);

                navigate("/");
            },
            setErrorMessage
        );
    };

    // Change Password
    const passwordChangeRequest = (oldPassword, newPassword, setErrorMessage, logoutCall) => {

        handleRequest(
            "PUT",
            API_ENDPOINTS.CHANGE_PASSWORD,
            { oldPassword, newPassword },
            () => {
                NotificationManager.success(<Message string={'app.password.change.success.message'} />, <Message string={'app.request.success.title'} />);
                logoutCall();
            },
            setErrorMessage
        );
    };

    // Change Locale
    const localeChangeRequest = (locale) => {
        console.info(locale)
        handleRequest(
            "PUT",
            API_ENDPOINTS.CHANGE_LOCALE,
            { locale },
            () => console.log("Locale changed successfully"),
            (error) => console.error("Failed to change locale", error)
        );
    };

    // Get linke token for user account link
    const checkAuthRequest = () => {
        return new Promise((resolve) => {
            handleRequest(
                "GET",
                API_ENDPOINTS.CHECK_AUTH,
                null,
                (response) => {
                    if (response && response.status === 200) {
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

    return {
        emailChangeRequest, passwordChangeRequest, localeChangeRequest, checkAuthRequest
    };
};
