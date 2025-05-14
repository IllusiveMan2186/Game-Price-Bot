import React, { useCallback } from 'react';
import { useGameActions, } from '@hooks/game/useGameActions';
import { useNavigation } from "@contexts/NavigationContext";
import Select from 'react-select';
import { NotificationManager } from 'react-notifications';

import * as constants from '@util/constants';
import Message from '@util/message';
import Search from '@components/game/list/header/search/Search';

import './GameListPageHeader.css';

const GameListPageHeader = ({ searchParams, updateSearchParams, nameValue, mode, reloadPage, pageSize }) => {
    const navigate = useNavigation();
    const sortBy = searchParams.get('sortBy') || 'name-ASC';
    const [name, setName] = React.useState(nameValue);
    const { getGameByUrlRequest } = useGameActions();

    if (!pageSize) {
        pageSize = 25;
    }

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
        if (!name) {
            NotificationManager.error(<Message string={'app.game.error.name.empty'} />, <Message string={'app.game.error.title'} />);

            return;
        }

        const validNamePattern = /^[a-zA-Z0-9\s&!:'()_.,-]*$/;
        const validUrlPattern = /^(https?:\/\/)?([a-zA-Z0-9-]+(\.[a-zA-Z]{2,})+)(\/.*)?$/;

        if (validUrlPattern.test(name)) {
            try {
                new URL(name);
                getGameByUrlRequest(name, navigate);
            } catch {
                NotificationManager.error(<Message string={'app.game.error.url.not.supported'} />, <Message string={'app.game.error.title'} />);
            }
        } else if (validNamePattern.test(name)) {
            navigate(`/search/${name}/${searchParams.toString()}`);
            navigate(0);
        } else {
            NotificationManager.error(<Message string={'app.game.error.name.incorrect'} />, <Message string={'app.game.error.title'} />);
        }
    }, [navigate, name, searchParams]);

    const findArrayElementByValue = (array, value) => {
        const el = array.find((element) => element.value === value);
        return el;
    };

    return (
        <div className="app-list-header">
            {!(mode === 'usersGames') && (
                <Search
                    handleSearchChange={handleSearchChange}
                    handleSearch={handleSearch}
                />
            )}
            <div className='app-list-header-sort'>
                <div className="app-header__sort">
                    <Select
                        aria-label="Page Size"
                        defaultValue={findArrayElementByValue(constants.pageSizesOptions, pageSize)}
                        options={constants.pageSizesOptions}
                        onChange={handlePageSizeChange}
                        styles={constants.selectStyles}
                        components={{ IndicatorSeparator: () => null }}
                        isSearchable={false}
                    />
                </div>
                <div className="app-header__sort">
                    <Select
                        aria-label="Sort By"
                        classNamePrefix=""
                        defaultValue={findArrayElementByValue(constants.sortsOptions, sortBy)}
                        options={constants.sortsOptions}
                        onChange={handleSortByChange}
                        styles={constants.selectStyles}
                        components={{ IndicatorSeparator: () => null }}
                        isSearchable={false}
                    />
                </div>
            </div>
        </div>
    );
};

export default GameListPageHeader;
