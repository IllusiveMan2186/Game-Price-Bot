import { handleRequest, handleError } from '@util/httpHelper';

// Centralized API Endpoints
const API_ENDPOINTS = {
    GAMES: '/user/games',
    GAME: '/game'
};

// Get games
export const getGamesRequest = (searchParameters, setElementAmount, setGames) => {
    handleRequest(
        'GET',
        searchParameters,
        null,
        (response) => {
            const { elementAmount, games } = response.data;
            setElementAmount(elementAmount);
            setGames(games);
        },
        handleError
    );
};

// Get single game
export const getGameRequest = (gameId, setGame, navigate) => {
    handleRequest(
        'GET',
        `${API_ENDPOINTS.GAME}/${gameId}`,
        null,
        (response) => setGame(response.data),
        (error) => handleError(error, navigate)
    );
};

// Subscribe to game
export const subscribeForGameRequest = (gameId) => {
    handleRequest(
        'POST',
        `${API_ENDPOINTS.GAMES}/${gameId}`,
        {},
        () => console.info(gameId),
        handleError
    );
};

// Unsubscribe from game
export const unsubscribeForGameRequest = (gameId) => {
    handleRequest(
        'DELETE',
        `${API_ENDPOINTS.GAMES}/${gameId}`,
        {},
        () => console.info(gameId),
        handleError
    );
};

// Remove game
export const removeGameRequest = (gameId, navigate) => {
    handleRequest(
        'DELETE',
        `${API_ENDPOINTS.GAME}/${gameId}`,
        {},
        () => navigate('/'),
        handleError
    );
};
