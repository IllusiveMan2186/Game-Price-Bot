import { useNavigation } from "@contexts/NavigationContext";
import { useHttpHelper } from "@hooks/useHttpHelper";

const API_ENDPOINTS = {
    RESEND_EMAIL: `/email/resend`,
    CHANGE_EMAIL: `/user/email`,
    CHANGE_PASSWORD: '/user/password',
    CHANGE_LOCALE: `/user/locale`,
    CHECK_AUTH: `/check-auth`,
};

export const useActivationActions = () => {
    const { handleRequest } = useHttpHelper();
    const navigate = useNavigation();

    // Activate user account
    const activateUserAccountRequest = (token) => {

        handleRequest(
            "POST",
            API_ENDPOINTS.ACTIVATE_USER,
            { token },
            () => navigate("/login"),
            () => console.error("Failed to activate user")
        );
    };

    // Resend Activation Email
    const resendActivationEmailRequest = (email) => {

        handleRequest(
            "POST",
            API_ENDPOINTS.RESEND_EMAIL,
            { email },
            () => navigate("/login"),
            (error) => console.error("Failed to resend activation email")
        );
    };


    return { activateUserAccountRequest, resendActivationEmailRequest };
};
