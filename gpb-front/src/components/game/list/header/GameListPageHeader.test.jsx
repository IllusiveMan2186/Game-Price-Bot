import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';

import GameListPageHeader from './GameListPageHeader';
import paramsReducer from '@features/params/paramsSlice';
import { useNavigation } from '@contexts/NavigationContext';
import { reloadPage } from '@util/navigationUtils';
import * as constants from '@util/constants';

jest.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key) => key }),
}));

jest.mock('@contexts/NavigationContext', () => ({
    useNavigation: jest.fn(),
}));

jest.mock('@components/game/list/header/search/Search', () => () => (
    <div data-testid="search-component" />
));

jest.mock('@util/navigationUtils', () => ({
    reloadPage: jest.fn(),
}));

describe('GameListPageHeader', () => {
    let store;
    const mockNavigate = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
        useNavigation.mockReturnValue(mockNavigate);

        store = configureStore({
            reducer: { params: paramsReducer },
            preloadedState: {
                params: {
                    sortBy: 'name',
                    pageSize: 25,
                    mode: 'list',
                },
            },
        });
    });

    const renderHeader = () =>
        render(
            <Provider store={store}>
                <GameListPageHeader />
            </Provider>
        );

    it('should renders the Search component when mode is not usersGames', () => {
        renderHeader();
        expect(screen.getByTestId('search-component')).toBeInTheDocument();
    });

    it('should does not render Search when mode is usersGames', () => {
        store = configureStore({
            reducer: { params: paramsReducer },
            preloadedState: { params: { sortBy: 'name', pageSize: 25, mode: 'usersGames' } },
        });
        render(
            <Provider store={store}>
                <GameListPageHeader />
            </Provider>
        );
        expect(screen.queryByTestId('search-component')).toBeNull();
    });

    it('should dispatches setSortBy and calls reloadPage when sorting changes', async () => {
        renderHeader();
        const sortControl = screen.getByLabelText('Sort By');
        await userEvent.click(sortControl);

        const newOption = constants.sortsOptions.find(opt => opt.value !== 'name');
        const newOptionLabel =
            typeof newOption.label === 'string'
                ? newOption.label
                : newOption.label.props.string;
        const optionNode = await screen.findByText(newOptionLabel);
        await userEvent.click(optionNode);

        expect(store.getState().params.sortBy).toBe(newOption.value);
        expect(reloadPage).toHaveBeenCalledWith(mockNavigate);
    });

    it('should dispatches setPageSize, resets pageNum and calls reloadPage when page size changes', async () => {
        renderHeader();
        const sizeControl = screen.getByLabelText('Page Size');
        await userEvent.click(sizeControl);

        const newSize = constants.pageSizesOptions.find(opt => opt.value !== 25);
        const newSizeLabel =
            typeof newSize.label === 'string'
                ? newSize.label
                : newSize.label.props.string;
        const sizeNode = await screen.findByText(newSizeLabel);
        await userEvent.click(sizeNode);

        expect(store.getState().params.pageSize).toBe(newSize.value);
        expect(store.getState().params.pageNum).toBe(1);
        expect(reloadPage).toHaveBeenCalledWith(mockNavigate);
    });
});
