import React, { useEffect, useCallback } from 'react';
import { useLocation, useParams, useNavigate } from 'react-router-dom';

import * as constants from '../../../../util/constants';
import { getGamesRequest } from '../../../../services/gameRequests';

import GameListFilter from '../filter/GameListFilter';
import GameListLoader from '../loader/GameListLoader';
import GameListPageHeader from '../header/GameListPageHeader';

import './GameListPage.css';

const GameListPage = ({ mode: propMode }) => {
    const location = useLocation();
    const { url, searchName } = useParams(); // Extract URL params
    const mode = propMode; // Use mode passed from the route
    const navigate = useNavigate();

    const [searchParams, setSearchParams] = React.useState(new URLSearchParams(url));

    const getParameterOrDefaultValue = (parameter, defaultValue) => {
        return parameter !== null ? parameter : defaultValue;
    }

    const pageFromParams = searchParams.get('pageNum') || 1;
    const [page, setPage] = React.useState(pageFromParams);
    const pageSize = searchParams.get('pageSize') || constants.pageSizesOptions[0].label;

    const [elementAmount, setElementAmount] = React.useState(0);
    const [games, setGames] = React.useState(null);
    const [name, setName] = React.useState(searchName);

    const parameterSetOrRemove = (parameter, value, defaultValue) => {
        if (value !== defaultValue) {
            console.info(parameter + " " + value + " " + defaultValue)
            searchParams.set(parameter, value);
        } else {
            searchParams.delete(parameter);
        }
    }

    const updateSearchParams = useCallback((key, value, defaultValue) => {
        if (value === defaultValue) {
            searchParams.delete(key);
        } else {
            searchParams.set(key, value);
        }
        setSearchParams(new URLSearchParams(searchParams.toString())); // Trigger state update
    }, [searchParams]);

    const reloadPage = useCallback(() => {
        console.info(page);
        const path =
            mode === 'usersGames' ? `/user/games/` :
            mode === 'search' ? `/search/${name}/` :
            '/games/';
        navigate(`${path}${searchParams.toString()}`);
        navigate(0);
    }, [mode, name, searchParams, navigate]);


    const getSearchParametrs = () => {
        switch (mode) {
            case "usersGames":
                return "/game/user/games?" + searchParams.toString()
            case "search":
                return "/game/name/" + name + "?" + searchParams.toString()
            default:
                return "/game/genre?" + searchParams.toString()
        }
    };

    useEffect(() => {
        const fetchGame = async () => {
            try {
                await getGamesRequest(getSearchParametrs(), setElementAmount, setGames);
            } catch (error) {
                console.error('Failed to fetch game details:', error);
                navigate('/error'); // Redirect to error page
            }
        };
        fetchGame();
    }, [navigate, setElementAmount, setGames, getSearchParametrs()]);

    if (!games) return <div>Loading...</div>;
    console.info(searchParams)
    return (
        <>
            <div class='App-game'>
                {(mode === "list") &&
                    <GameListFilter
                        getParameterOrDefaultValue={getParameterOrDefaultValue}
                        searchParams={searchParams}
                        reloadPage={reloadPage}
                        setPage={setPage}
                        parameterSetOrRemove={parameterSetOrRemove}
                    />
                }

                <div className={(mode === "list") ? "App-game-content" : "App-game-search-content"}>
                    <GameListPageHeader
                        searchParams={searchParams}
                        updateSearchParams={updateSearchParams}
                        name={name}
                        setName={setName}
                        reloadPage={reloadPage}
                        mode={mode}
                        navigate={navigate}
                    />
                    <GameListLoader
                        games={games}
                        elementAmount={elementAmount}
                        page={page}
                        updateSearchParams={updateSearchParams}
                        pageSize={pageSize}
                        reloadPage={reloadPage}
                    />
                </div>
            </div >
        </>
    );

};

export default GameListPage;
