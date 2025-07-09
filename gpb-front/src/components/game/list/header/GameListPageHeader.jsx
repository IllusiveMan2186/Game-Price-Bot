import React, { useCallback } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigation } from '@contexts/NavigationContext';
import Select from 'react-select';

import { setSortBy, setPageSize, setPageNum, } from '@features/params/paramsSlice';

import { reloadPage } from '@util/navigationUtils';
import * as constants from '@util/constants';
import Search from '@components/game/list/header/search/Search';

import './GameListPageHeader.css';

const GameListPageHeader = ({ }) => {
    const dispatch = useDispatch();
    const navigate = useNavigation();

    const { sortBy, pageSize, mode } = useSelector((state) => state.params);

    const handleSortByChange = useCallback(
        (selectedOption) => {
            dispatch(setSortBy(selectedOption.value));
            reloadPage(navigate);
        },
        []
    );

    const handlePageSizeChange = useCallback(
        (selectedOption) => {
            dispatch(setPageSize(selectedOption.value));
            dispatch(setPageNum(1)); // reset to first page
            reloadPage(navigate);
        },
        []
    );

    const findArrayElementByValue = (array, value) => +
        array.find((element) => element.value === value);

    return (
        <div className="app-list-header">
            {mode !== 'usersGames' && (
                <Search />
            )}

            <div className="app-list-header-sort">
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
