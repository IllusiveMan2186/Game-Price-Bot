import axios from 'axios';

export const getAuthToken = () => {
    return window.localStorage.getItem('auth_token');
};

export function isUserAuth() {
    return getAuthToken() !== null && getAuthToken() !== "null" ? true : false;
};

export const setAuthHeader = (token) => {
    window.localStorage.setItem('auth_token', token);
};

export const setEmailHeader = (email) => {
    window.localStorage.setItem('email', email);
};

export const getEmail = () => {
    return window.localStorage.getItem('email');
};

export const setRoleHeader = (role) => {
    window.localStorage.setItem('role', role);
};

export const getRole = () => {
    return window.localStorage.getItem('role');
};

export function isUserAdmin() {
    return getRole() !== null && getRole() === "ROLE_ADMIN" ? true : false;
};

export const defaultRequestErrorCheck = (error) => {
    console.log(error.response.status)
    console.log(error.response.data.error)
    if (error.response.status === 401) {
        setAuthHeader(null);
        setRoleHeader(null);
    }
};

axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.headers.post['Content-Type'] = 'application/json';

export const request = (method, url, data) => {

    let headers = {};
    if (getAuthToken() !== null && getAuthToken() !== "null") {
        headers = { 'Authorization': `Bearer ${getAuthToken()}` };
    }

    return axios({
        method: method,
        url: url,
        headers: headers,
        data: data
    });
};