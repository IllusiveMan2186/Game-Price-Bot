import { request, setAuthHeader, setEmailHeader, defaultRequestErrorCheck } from '../util/axios_helper';

export const resendActivationEmailRequest = (event, email, navigate) => {
    event.preventDefault();
    console.info(email)
    request(
        "POST",
        "/user/resend/email/" + email
    )
    navigate("/login")
};

export const emailChangeRequest = (event, email, setErrorMessage, navigate) => {
    event.preventDefault();
    request(
        "PUT",
        "/user/email/" + email,
        {
            email: email
        }).then(
            (response) => {
                setEmailHeader(response.data.email)
                setAuthHeader(response.data.token)
                navigate("/")
            }).catch(
                (error) => {
                    defaultRequestErrorCheck(error)
                    if (error.response.status === 401) {
                        navigate("/login")
                    }
                    setErrorMessage(error.response.data)
                }
            );
};

export const passwordChangeRequest = (event, password, setErrorMessage, navigate) => {
    event.preventDefault();
    request(
        "PUT",
        "/user/password",
        {
            password: password
        }).then(
            (response) => {
                navigate("/")
            }).catch(
                (error) => {
                    defaultRequestErrorCheck(error)
                    if (error.response.status === 401) {
                        navigate("/login")
                    }
                    setErrorMessage(error.response.data)
                }
            );
};

export const localeChangeRequest = (locale) => {
    request(
        "PUT",
        "/user/locale/" + locale)
};