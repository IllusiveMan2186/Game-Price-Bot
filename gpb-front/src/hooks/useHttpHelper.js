import { useAuth } from "@contexts/AuthContext";
import { request } from "@services/httpService";

export const useHttpHelper = () => {
    const { getLinkToken, getAccessToken } = useAuth();

    const handleRequest = async (method, url, data, onSuccess, onError) => {
        try {
            const response = await request(method, url, data, getAccessToken(), getLinkToken());
            onSuccess(response);
        } catch (error) {
            console.info(error);
            onError?.(error?.response?.data);
        }
    };

    return { handleRequest };
};
