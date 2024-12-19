import axios from 'axios';
import { getAuthToken, setAuthToken, setUserRole } from '@util/authService'

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

export const handleRequest = async (method, url, data, onSuccess, onError) => {
    try {
        const response = await request(method, url, data);
        onSuccess(response);
    } catch (error) {
        console.info(error)
        if (error?.response?.status === 401 && getAuthToken() !== null) {
            setAuthToken(null);
            setUserRole(null);
        }
        onError(error.response.data);
    }
};
