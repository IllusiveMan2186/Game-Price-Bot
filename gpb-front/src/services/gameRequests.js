import { handleRequest, handleError } from '@util/httpHelper';

// Centralized API Endpoints
const API_ENDPOINTS = {
    GAMES: '/user/games',
    GAME: '/game',
    GAME_STORE: '/game/store',
    GAME_URL: '/game/url',
    GAME_URL_GET: '/game/url?url='
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
        () => {
            setTimeout(() => {
                navigate('/');
            }, 100);
        },
        handleError
    );
};

// Remove game in store
export const removeGameInStoreRequest = (gameInStoreId, onSuccess) => {
    handleRequest(
        'DELETE',
        `${API_ENDPOINTS.GAME_STORE}/${gameInStoreId}`,
        {},
        () => {
            onSuccess();
        },
        handleError
    );
};

// Get game by url
export const getGameByUrlRequest = (url, navigate) => {
    handleRequest(
        'GET',
        `${API_ENDPOINTS.GAME_URL_GET}${url}`,
        null,
        (response) => { navigate(`/game/${response.data.id}`) },
        (error) => handleError(error, navigate)
    );
};

// Get game by url
export const addGameInStoreByUrlRequest = (gameId, url) => {
    handleRequest(
        'POST',
        `${API_ENDPOINTS.GAME_URL}`,
        { gameId, url },
        (response) => { },
        (error) => handleError(error)
    );
};