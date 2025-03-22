import React from 'react';
import { useTranslation } from 'react-i18next';
import Message from '@util/message';

import './Search.css';

const Search = ({ handleSearchChange, handleSearch }) => {
    const { t } = useTranslation();

    const placeholder = t('app.game.filter.search.title');

    return (
        <div className="app-header__search">
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

export default Search;