import React, { useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigation } from '@contexts/NavigationContext';
import { useSelector, useDispatch } from 'react-redux';
import { NotificationManager } from 'react-notifications';
import { useGameActions } from '@hooks/game/useGameActions';
import { setSearch } from '@features/params/paramsSlice';
import Message from '@util/message';
import { buildSearchParams } from '@util/searchParamsUtils';

import './Search.css';

const Search = ({ }) => {
    const { t } = useTranslation();
    const navigate = useNavigation();
    const dispatch = useDispatch();

    const { getGameByUrlRequest } = useGameActions();

    const { search } = useSelector((state) => state.params);

    const handleSearch = useCallback(() => {
        if (!search) {
            NotificationManager.error(
                <Message string="app.game.error.name.empty" />,
                <Message string="app.game.error.title" />
            );
            return;
        }

        const validNamePattern = /^[a-zA-Z0-9\s&!:'()_.,-]*$/;
        const validUrlPattern = /^(https?:\/\/)?([a-zA-Z0-9-]+(\.[a-zA-Z]{2,})+)(\/.*)?$/;

        if (validUrlPattern.test(search)) {
            try {
                new URL(search);
                getGameByUrlRequest(search, navigate);
            } catch {
                NotificationManager.error(
                    <Message string="app.game.error.url.not.supported" />,
                    <Message string="app.game.error.title" />
                );
            }
        } else if (validNamePattern.test(search)) {
            const queryString = buildSearchParams();
            navigate(`/search/${search}?${queryString}`);
            navigate(0);
        } else {
            NotificationManager.error(
                <Message string="app.game.error.name.incorrect" />,
                <Message string="app.game.error.title" />
            );
        }
    }, [navigate, search, getGameByUrlRequest])

    const handleSearchChange = (e) => {
        dispatch(setSearch(e.target.value));
    };

    const placeholder = t('app.game.filter.search.title');

    return (
        <div className="app-header__search">
            <input
                id="game-search-input-field"
                type="search"
                placeholder={placeholder}
                value={search}
                onChange={handleSearchChange}
                onKeyDown={(e) => {
                    if (e.key === 'Enter') handleSearch();
                }}
            />
            <button id="game-search-button" onClick={handleSearch}>
                <Message string={'app.game.filter.search.button'} />
            </button>
        </div>
    );
};

export default Search;