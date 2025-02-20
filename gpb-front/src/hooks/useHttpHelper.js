import { useAuth } from "@contexts/AuthContext";
import { request } from "@services/httpService";
import { useNavigation } from "@contexts/NavigationContext";

export const useHttpHelper = () => {
    const { getLinkToken, getAccessToken } = useAuth();
    const navigate = useNavigation();

    const handleRequest = async (method, url, data, onSuccess, onError) => {
        try {
            const response = await request(method, url, data, getAccessToken(), getLinkToken());
            onSuccess(response);
        } catch (error) {
            console.info(error);

            const status = error?.response?.status;
            if (status && [500, 502, 503].includes(status)) {
                console.info("server error")
                navigate("/error", { state: { errorMessage: error?.response?.data } });
                return;
            }

            onError?.(error?.response?.data);
        }
    };

    return { handleRequest };
};
