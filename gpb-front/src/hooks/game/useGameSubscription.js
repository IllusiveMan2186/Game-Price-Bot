import { useHttpHelper } from "@hooks/useHttpHelper";

const API_ENDPOINTS = {
    GAMES: '/user/games',
};

export const useGameSubscription = () => {
    const { handleRequest } = useHttpHelper();

    // Subscribe to game
    const subscribeForGameRequest = (gameId) => {
        handleRequest(
            'POST',
            `${API_ENDPOINTS.GAMES}/${gameId}`,
            {},
            () => console.info(gameId)
        );
    };

    // Unsubscribe from game
    const unsubscribeForGameRequest = (gameId) => {
        handleRequest(
            'DELETE',
            `${API_ENDPOINTS.GAMES}/${gameId}`,
            {},
            () => console.info(gameId)
        );
    };

    return { subscribeForGameRequest, unsubscribeForGameRequest };
};
