import { useNavigation } from "@contexts/NavigationContext";
import { useHttpHelper } from "@hooks/useHttpHelper";

const API_ENDPOINTS = {
    GAME_STORE: '/game/store',
    GAME_URL: '/game/url',
    GAME_URL_GET: '/game/url?url='
};

export const useGameStoreActions = () => {
    const navigate = useNavigation();
    const { handleRequest } = useHttpHelper();

    // Remove game from store
    const removeGameInStoreRequest = (gameInStoreId, onSuccess) => {
        handleRequest(
            'DELETE',
            `${API_ENDPOINTS.GAME_STORE}/${gameInStoreId}`,
            {},
            () => {
                onSuccess();
            }
        );
    };

    // Add game to store by URL
    const addGameInStoreByUrlRequest = (gameId, url) => {
        handleRequest(
            'POST',
            `${API_ENDPOINTS.GAME_URL}`,
            { gameId, url },
            () => { }
        );
    };

    return { removeGameInStoreRequest, addGameInStoreByUrlRequest };
};
