import { request, setAuthHeader, setEmailHeader, setRoleHeader, setLocaleHeader, defaultRequestErrorCheck } from '../util/axios_helper';
import { changeLanguage } from 'i18next';

export const loginRequest = (e, email, password, setErrorMessage, navigate) => {
    e.preventDefault();
    request(
        "POST",
        "/login",
        {
            email: email,
            password: password
        }).then(
            (response) => {
                setAuthHeader(response.data.token)
                setEmailHeader(response.data.email)
                setRoleHeader(response.data.authorities[0].authority)
                setLocaleHeader(response.data.locale)
                changeLanguage(response.data.locale);
                navigate("/")
            }).catch(
                (error) => {
                    setErrorMessage(error.response.data)
                }
            );
};

export const registerRequest = (event, email, password, setErrorMessage, navigate) => {
    event.preventDefault();
    request(
        "POST",
        "/registration",
        {
            email: email,
            password: password
        }).then(
            (response) => {
                setAuthHeader(response.data.token)
                setEmailHeader(response.data.email)
                setRoleHeader(response.data.authorities[0].authority)
                navigate("/")
            }).catch(
                (error) => {
                    setErrorMessage(error.response.data)
                }
            );
};

export const emailChangeRequest = (event, email, setErrorMessage, navigate) => {
    event.preventDefault();
    request(
        "PUT",
        "/user/email",
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