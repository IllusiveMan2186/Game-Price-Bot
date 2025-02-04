import axios from 'axios';
import { getLinkToken} from '@util/userDataUtils'
import { getAuthToken, setAuthToken } from '@util/authUtils'
import { setUserRole } from '@util/userDataUtils'
import config from "@root/config";

axios.defaults.baseURL = config.BACKEND_SERVICE_URL;
axios.defaults.headers.post['Content-Type'] = 'application/json';
axios.defaults.withCredentials = true;


export const request = (method, url, data) => {

    let headers = {};
    if (getAuthToken() !== null && getAuthToken() !== "null") {
        headers = { 'Authorization': `Bearer ${getAuthToken()}` };
    } else if (getLinkToken() !== null && getLinkToken() !== "null") {
        headers = { 'LinkToken': `${getLinkToken()}` };
    }

    console.info(method +" "+ url)

    return axios({
        method: method,
        url: url,
        headers: headers,
        data: data,
        withCredentials: true,
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
        onError?.(error.response?.data);
    }
};

// Centralized Error Handler
export const handleError = (error, navigate, setErrorMessage) => {
    if (error?.response?.status === 401) {
        setAuthToken(null);
        setUserRole(null);
        navigate("/login");
    }
    setErrorMessage?.(error?.response?.data || "An unexpected error occurred.");
};