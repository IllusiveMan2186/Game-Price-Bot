import { handleRequest } from '@util/httpHelper';
import { defaultRequestErrorCheck } from '@util/authService';
import { setAuthToken, setUserRole } from '@util/authService';

// Centralized API Endpoints
const API_ENDPOINTS = {
    GAMES: '/user/games',
    GAME: '/game'
};

// Reusable error handler
const handleError = (error, navigate) => {
    defaultRequestErrorCheck(error);
    console.info(error)
    if (error?.response?.status === 401) {
        navigate?.(0); // Refresh on unauthorized
    }
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
export const subscribeForGameRequest = (gameId, navigate) => {
    handleRequest(
        'POST',
        `${API_ENDPOINTS.GAMES}/${gameId}`,
        {},
        () => navigate(0),
        handleError
    );
};

// Unsubscribe from game
export const unsubscribeForGameRequest = (gameId, navigate) => {
    handleRequest(
        'DELETE',
        `${API_ENDPOINTS.GAMES}/${gameId}`,
        {},
        () => navigate(0),
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
