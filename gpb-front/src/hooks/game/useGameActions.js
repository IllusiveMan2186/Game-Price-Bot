import { useNavigation } from "@contexts/NavigationContext";
import { useHttpHelper } from "@hooks/useHttpHelper";

const API_ENDPOINTS = {
    GAMES: '/user/games',
    GAME: '/game',
    GAME_URL_GET: '/game/url?url='
};

export const useGameActions = () => {
    const navigate = useNavigation();
    const { handleRequest } = useHttpHelper();

    // Get games
    const getGamesRequest = (searchParameters, setElementAmount, setGames) => {
        handleRequest(
            'GET',
            searchParameters,
            null,
            (response) => {
                const { elementAmount, games } = response.data;
                setElementAmount(elementAmount);
                setGames(games);
            }
        );
    };

    // Get single game
    const getGameRequest = (gameId, setGame, onError) => {
        handleRequest(
            'GET',
            `${API_ENDPOINTS.GAME}/${gameId}`,
            null,
            (response) => setGame(response.data)
        );
    };

    // Get game by URL
    const getGameByUrlRequest = (url) => {
        handleRequest(
            'GET',
            `${API_ENDPOINTS.GAME_URL_GET}${url}`,
            null,
            (response) => { navigate(`/game/${response.data.id}`) }
        );
    };

    // Remove game
    const removeGameRequest = (gameId) => {
        handleRequest(
            'DELETE',
            `${API_ENDPOINTS.GAME}/${gameId}`,
            {},
            () => {
                setTimeout(() => {
                    navigate('/');
                }, 100);
            }
        );
    };

    return { getGamesRequest, getGameRequest, getGameByUrlRequest, removeGameRequest };
};
