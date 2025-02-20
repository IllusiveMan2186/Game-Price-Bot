import { useNavigation } from "@contexts/NavigationContext";
import { useHttpHelper } from "@hooks/useHttpHelper";
import { NotificationManager } from 'react-notifications';

import Message from '@util/message';

const API_ENDPOINTS = {
    CHANGE_EMAIL: `/email`,
    EMAIL_CONFIRM: '/email/change/confirm',
};

export const useEmailActions = () => {
    const { handleRequest } = useHttpHelper();
    const navigate = useNavigation();

    // Change Email
    const emailChangeRequest = (email, setErrorMessage) => {
        handleRequest(
            "PUT",
            API_ENDPOINTS.CHANGE_EMAIL,
            { email },
            (response) => {
                NotificationManager.success(<Message string={'app.email.change.check.emails'} />, <Message string={'app.request.success.title'} />);

                navigate("/");
            },
            setErrorMessage
        );
    };

    // Email conformation
    const emailConfirmRequest = (token) => {
        handleRequest(
            "POST",
            API_ENDPOINTS.EMAIL_CONFIRM,
            { token },
            (response) => {
                NotificationManager.success(<Message string={response.data} />, <Message string={'app.request.success.title'} />);
                navigate("/");
            },
            (error) => {
                NotificationManager.error(<Message string={error} />, <Message string={'app.game.error.title'} />);
                navigate("/");
            }
        );
    };

    return {
        emailChangeRequest, emailConfirmRequest
    };
};
