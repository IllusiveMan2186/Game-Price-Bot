import React, { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Select from 'react-select';

import * as constants from '../../../../util/constants';
import Message from '../../../../util/message';

import './GameListPageHeader.css';

const Search = ({ handleSearchChange, handleSearch }) => {
    const { t } = useTranslation();

    const placeholder = t('app.game.filter.search.title');

    return (
        <div className="App-game-content-header-search">
            <input
                id="game-search-input-field"
                type="search"
                placeholder={placeholder}
                onChange={handleSearchChange}
            />
            <button id="game-search-button" onClick={handleSearch}>
                <Message string={'app.game.filter.search.button'} />
            </button>
        </div>
    );
};

const GameListPageHeader = ({ searchParams, updateSearchParams, name, setName, reloadPage, mode, pageSize }) => {
    const navigate = useNavigate();
    const sortBy = searchParams.get('sortBy') || 'name-ASC';

    const handleSortByChange = useCallback((selectedOption) => {
        updateSearchParams('sortBy', selectedOption.value, 'name-ASC');
        reloadPage();
    }, [updateSearchParams, reloadPage]);

    const handleSearchChange = useCallback((event) => {
        setName(event.target.value);
    }, [setName]);

    const handlePageSizeChange = useCallback((selectedOption) => {
        updateSearchParams('pageSize', selectedOption.value, constants.pageSizesOptions[0].label);
        updateSearchParams('pageNum', 1, 1);
        reloadPage();
    }, [updateSearchParams, reloadPage]);

    const handleSearch = useCallback(() => {
        navigate(`/search/${name}/${searchParams.toString()}`);
        navigate(0);
    }, [navigate, name, searchParams]);

    const findArrayElementByValue = (array, value) => {
        return array.find((element) => element.value === value);
    };

    return (
        <div className="App-game-content-header">
            {!(mode === 'usersGames') && (
                <Search
                    handleSearchChange={handleSearchChange}
                    handleSearch={handleSearch}
                />
            )}
            <div className="App-game-content-header-sort">
                <Select
                    classNamePrefix=""
                    defaultValue={findArrayElementByValue(constants.sortsOptions, sortBy)}
                    options={constants.sortsOptions}
                    onChange={handleSortByChange}
                    styles={constants.selectStyles}
                    components={{ IndicatorSeparator: () => null }}
                    isSearchable={false}
                />
            </div>
            <div className="App-game-content-header-sort">
                <Select
                    defaultValue={findArrayElementByValue(constants.pageSizesOptions, pageSize)}
                    options={constants.pageSizesOptions}
                    onChange={handlePageSizeChange}
                    styles={constants.selectStyles}
                    components={{ IndicatorSeparator: () => null }}
                    isSearchable={false}
                />
            </div>
        </div>
    );
};

export default GameListPageHeader;
