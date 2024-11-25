import { request, setAuthHeader, setEmailHeader, setRoleHeader, setLocaleHeader, getLocale } from '../util/axios_helper';
import { changeLanguage } from 'i18next';
import { NotificationManager } from 'react-notifications';

export const loginRequest = (email, password, setErrorMessage, navigate) => {
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

export const registerRequest = ( email, password, setErrorMessage, navigate) => {
    request(
        "POST",
        "/registration",
        {
            email: email,
            password: password,
            locale: getLocale()
        }).then(
            (response) => {
                NotificationManager.success('Success message', 'Title here');
                navigate("/")
            }).catch(
                (error) => {
                    setErrorMessage(error.response.data)
                }
            );
};