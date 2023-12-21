import { request, defaultRequestErrorCheck } from '../util/axios_helper';

export const getGamesRequest = (searchParameters, setElementAmount, setGames, navigate) => {
    request(
        "GET",
        searchParameters,
    ).then(
        (response) => {
            setElementAmount(response.data.elementAmount)
            setGames(response.data.games);
        }).catch(
            (error) => {
                defaultRequestErrorCheck(error)
                if (error.response.status === 401) {
                    navigate(0);
                }
            }
        )
};

export const getGameRequest = (gameId, setGame, navigate) => {
    request('GET', '/game/' + gameId).then((response) => {
        setGame(response.data);
    }).catch(
        (error) => {
            handleError(error, navigate)
        }
    );
};

export const subscribeForGameRequest = (gameId, navigate) => {
    request('POST', '/user/games/' + gameId, {}).then((response) => {
        navigate(0)
    }).catch(
        (error) => {
            handleError(error)
        }
    );
};

export const unsubscribeForGameRequest = (gameId, navigate) => {
    request('DELETE', '/user/games/' + gameId, {}).then((response) => {
        navigate(0)
    }).catch(
        (error) => {
            handleError(error)
        }
    );
};

export const removeGameRequest = (gameId, navigate) => {
    request('DELETE', '/game/' + gameId, {}).then((response) => {
        navigate("/")
    }).catch(
        (error) => {
            handleError(error)
        }
    );
};

const handleError = (error, navigate) => {
    defaultRequestErrorCheck(error)
    if (error.response.status === 401) {
        navigate(0);
    }
};