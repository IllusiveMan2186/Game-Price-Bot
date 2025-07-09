import { render, waitFor, screen, act, cleanup } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';

import GameListPage from './GameListPage';
import paramsReducer from '@features/params/paramsSlice';
import { useParams } from 'react-router-dom';
import { useNavigation } from '@contexts/NavigationContext';
import { useGameActions } from '@hooks/game/useGameActions';
import { buildSearchParams } from '@util/searchParamsUtils';

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useParams: jest.fn(),
}));
jest.mock('@contexts/NavigationContext', () => ({ useNavigation: jest.fn() }));
jest.mock('@hooks/game/useGameActions', () => ({ useGameActions: jest.fn() }));
jest.mock('@util/searchParamsUtils', () => ({ __esModule: true, buildSearchParams: jest.fn() }));

jest.mock('@components/game/list/filter/GameListFilter', () => () => <div data-testid="game-list-filter" />);
jest.mock('@components/game/list/loader/GameListLoader', () => () => <div data-testid="game-list-loader" />);
jest.mock('@components/game/list/header/GameListPageHeader', () => () => <div data-testid="game-list-page-header" />);

describe('GameListPage', () => {
    let store;
    const originalError = console.error;
    const originalLocation = window.location;

    beforeAll(() => {
        console.error = jest.fn();
    });

    afterAll(() => {
        console.error = originalError;
        Object.defineProperty(window, 'location', { configurable: true, value: originalLocation });
    });

    afterEach(() => {
        cleanup();
    });

    function setup(mode, fetchMock) {
        store = configureStore({ reducer: { params: paramsReducer } });
        useParams.mockReturnValue({ url: 'pageNum=2&pageSize=10', searchName: 'testGame' });
        buildSearchParams.mockReturnValue('pageNum=2&pageSize=10');
        const navigateMock = jest.fn();
        useNavigation.mockReturnValue(navigateMock);
        const getGamesMock = fetchMock || jest.fn().mockResolvedValue();
        useGameActions.mockReturnValue({ getGamesRequest: getGamesMock });

        render(
            <Provider store={store}>
                <GameListPage mode={mode} />
            </Provider>
        );

        return { navigateMock, getGamesMock };
    }

    it('should renders filter, header, loader and fetches games in list mode', async () => {
        const { getGamesMock } = setup('list');
        await waitFor(() => expect(getGamesMock).toHaveBeenCalled());
        expect(screen.getByTestId('game-list-filter')).toBeInTheDocument();
        expect(screen.getByTestId('game-list-page-header')).toBeInTheDocument();
        expect(screen.getByTestId('game-list-loader')).toBeInTheDocument();
    });

    it('should does not render filter when mode is search', async () => {
        const { getGamesMock } = setup('search');
        await waitFor(() => expect(getGamesMock).toHaveBeenCalled());
        expect(screen.queryByTestId('game-list-filter')).toBeNull();
        expect(screen.getByTestId('game-list-page-header')).toBeInTheDocument();
        expect(screen.getByTestId('game-list-loader')).toBeInTheDocument();
    });

    it('should calls getGamesRequest with correct URL for list mode', async () => {
        const { getGamesMock } = setup('list');
        await waitFor(() => expect(getGamesMock).toHaveBeenCalled());
        expect(getGamesMock).toHaveBeenCalledWith(
            '/game/genre?pageNum=2&pageSize=10',
            expect.any(Function),
            expect.any(Function)
        );
    });

    it('should calls getGamesRequest with correct URL for search mode', async () => {
        const { getGamesMock } = setup('search');
        await waitFor(() => expect(getGamesMock).toHaveBeenCalled());
        expect(getGamesMock).toHaveBeenCalledWith(
            '/game/name/testGame?pageNum=2&pageSize=10',
            expect.any(Function),
            expect.any(Function)
        );
    });

    it('navigates to /error on fetch failure', async () => {
        const failingFetch = jest.fn().mockRejectedValue(new Error('fail'));
        const { navigateMock, getGamesMock } = setup('list', failingFetch);
        await waitFor(() => expect(getGamesMock).toHaveBeenCalled());
        await waitFor(() => expect(navigateMock).toHaveBeenCalledWith('/error'));
    });

    it('should reloads page on popstate event', () => {
        const reloadSpy = jest.fn();
        Object.defineProperty(window, 'location', {
            configurable: true,
            value: { ...originalLocation, reload: reloadSpy },
        });
        setup('list');
        act(() => window.dispatchEvent(new Event('popstate')));
        expect(reloadSpy).toHaveBeenCalled();
    });
});
