import { useNavigation } from "@contexts/NavigationContext";
import { useHttpHelper } from "@hooks/useHttpHelper";

const API_ENDPOINTS = {
    RESEND_EMAIL: `/user/resend/email`,
    CHANGE_EMAIL: `/user/email`,
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
                navigate("/");
            },
            setErrorMessage
        );
    };

    // Change Password
    const passwordChangeRequest = (event, password, setErrorMessage) => {
        handleRequest(
            "PUT",
            API_ENDPOINTS.CHANGE_PASSWORD,
            { password },
            () => navigate("/"),
            setErrorMessage
        );
    };

    // Change Locale
    const localeChangeRequest = (locale) => {
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
