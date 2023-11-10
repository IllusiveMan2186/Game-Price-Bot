import axios from 'axios';

export const getAuthToken = () => {
    return window.localStorage.getItem('auth_token');
};

export function isUserAuth () {
    return getAuthToken() !== null && getAuthToken() !== "null"? true : false;
};

export const setAuthHeader = (token) => {
    window.localStorage.setItem('auth_token', token);
};

export const setEmailHeader = (email) => {
    window.localStorage.removeItem('email');
    window.localStorage.setItem('email', email);
};

export const getEmail = () => {
    return window.localStorage.getItem('email');
};

axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.headers.post['Content-Type'] = 'application/json';

export const request = (method, url, data) => {

    let headers = {};
    if (getAuthToken() !== null && getAuthToken() !== "null") {
        headers = {'Authorization': `Bearer ${getAuthToken()}`};
    }

    return axios({
        method: method,
        url: url,
        headers: headers,
        data: data});
};