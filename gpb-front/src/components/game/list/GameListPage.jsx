import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useNavigation } from '@contexts/NavigationContext';
import { useSelector, useDispatch } from 'react-redux';

import { useGameActions } from '@hooks/game/useGameActions';

import {
    setMode,
    setSearch,
    setGenres,
    setTypes,
    setSortBy,
    setMinPrice,
    setMaxPrice,
    setPageNum,
    setPageSize,
    setGames,
    setElementAmount
} from '@features/params/paramsSlice';

import { buildSearchParams } from '@util/searchParamsUtils';

import GameListFilter from '@components/game/list/filter/GameListFilter';
import GameListLoader from '@components/game/list/loader/GameListLoader';
import GameListPageHeader from '@components/game/list/header/GameListPageHeader';

import './GameListPage.css';

const GameListPage = ({ mode: propMode }) => {
    useEffect(() => {
        const handlePopState = () => {
            window.location.reload();
        };

        window.addEventListener('popstate', handlePopState);
        return () => {
            window.removeEventListener('popstate', handlePopState);
        };
    }, []);

    const dispatch = useDispatch();
    const { name, mode, search } = useSelector((state) => state.params);

    const { url, searchName } = useParams();
    const navigate = useNavigation();
    const { getGamesRequest } = useGameActions();
    const [hasInitialized, setHasInitialized] = useState(false);

    // 🔄 Sync URL params into Redux on mount
    useEffect(() => {
        const searchParams = new URLSearchParams(url || '');

        if (propMode) dispatch(setMode(propMode));
        if (searchName) dispatch(setSearch(searchName));
        if (searchParams.has('pageNum')) dispatch(setPageNum(Number(searchParams.get('pageNum'))));
        if (searchParams.has('pageSize')) dispatch(setPageSize(Number(searchParams.get('pageSize'))));
        if (searchParams.has('sortBy')) dispatch(setSortBy(searchParams.get('sortBy')));
        if (searchParams.has('minPrice')) dispatch(setMinPrice(Number(searchParams.get('minPrice'))));
        if (searchParams.has('maxPrice')) dispatch(setMaxPrice(Number(searchParams.get('maxPrice'))));
        if (searchParams.has('genre')) dispatch(setGenres(searchParams.get('genre').split(',')));
        if (searchParams.has('type')) dispatch(setTypes(searchParams.get('type').split(',')));

        setHasInitialized(true)
    }, []);

    // 🔁 Handle browser back/forward
    useEffect(() => {
        const handlePopState = () => {
            window.location.reload();
        };
        window.addEventListener('popstate', handlePopState);
        return () => window.removeEventListener('popstate', handlePopState);
    }, []);

    // 🔧 Builds full backend URL from current Redux params
    const getSearchUrl = () => {
        const query = buildSearchParams();
        switch (mode) {
            case 'usersGames':
                return `/game/user/games?${query}`;
            case 'search':
                return `/game/name/${search}?${query}`;
            default:
                return `/game/genre?${query}`;
        }
    };

    useEffect(() => {
        if (!hasInitialized) return;

        const fetchGames = async () => {
            try {
                await getGamesRequest(
                    getSearchUrl(),
                    (amount) => dispatch(setElementAmount(amount)),
                    (games) => dispatch(setGames(games))
                );
            } catch (error) {
                console.error('Failed to fetch games:', error);
                navigate('/error');
            }
        };

        fetchGames();
    }, [hasInitialized]);

    return (
        <div className='app-game'>
            {mode === 'list' && <GameListFilter />}

            <div className={mode === 'list' ? 'app-game-content' : 'app-game-search-content'}>
                <GameListPageHeader />
                <GameListLoader />
            </div>
        </div>
    );
};

export default GameListPage;
