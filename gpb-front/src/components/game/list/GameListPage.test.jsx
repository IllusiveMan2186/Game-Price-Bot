import React from 'react';
import { render, waitFor, screen, cleanup, act } from '@testing-library/react';
import GameListPage from './GameListPage';
import { useParams } from 'react-router-dom';
import { useNavigation } from '@contexts/NavigationContext';
import { useGameActions } from '@hooks/game/useGameActions';

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useParams: jest.fn(),
}));

jest.mock('@contexts/NavigationContext', () => ({
    useNavigation: jest.fn(),
}));

jest.mock('@hooks/game/useGameActions', () => ({
    useGameActions: jest.fn(),
}));

jest.mock('@components/game/list/filter/GameListFilter', () => () => (
    <div data-testid="game-list-filter" />
));
jest.mock('@components/game/list/loader/GameListLoader', () => () => (
    <div data-testid="game-list-loader" />
));
jest.mock('@components/game/list/header/GameListPageHeader', () => () => (
    <div data-testid="game-list-page-header" />
));

jest.mock('@util/constants', () => ({
    pageSizesOptions: [{ label: '10' }],
}));

describe('GameListPage', () => {
    const mockNavigate = jest.fn();
    const mockGetGamesRequest = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
        useNavigation.mockReturnValue(mockNavigate);
        useParams.mockReturnValue({
            url: '?pageNum=2&pageSize=10',
            searchName: 'testGame',
        });
        useGameActions.mockReturnValue({
            getGamesRequest: mockGetGamesRequest,
        });
        mockGetGamesRequest.mockResolvedValue();
    });

    afterEach(() => {
        cleanup();
    });

    it('should renders GameListFilter, GameListPageHeader, and GameListLoader in "list" mode', async () => {
        render(<GameListPage mode="list" />);

        await waitFor(() => {
            expect(mockGetGamesRequest).toHaveBeenCalled();
        });

        expect(screen.getByTestId('game-list-filter')).toBeInTheDocument();
        expect(screen.getByTestId('game-list-page-header')).toBeInTheDocument();
        expect(screen.getByTestId('game-list-loader')).toBeInTheDocument();
    });

    it('should does not render GameListFilter when mode is not "list"', async () => {
        render(<GameListPage mode="search" />);

        await waitFor(() => {
            expect(mockGetGamesRequest).toHaveBeenCalled();
        });

        expect(screen.queryByTestId('game-list-filter')).toBeNull();
        expect(screen.getByTestId('game-list-page-header')).toBeInTheDocument();
        expect(screen.getByTestId('game-list-loader')).toBeInTheDocument();
    });

    it('should calls getGamesRequest with correct URL for "list" mode', async () => {
        render(<GameListPage mode="list" />);

        await waitFor(() => {
            expect(mockGetGamesRequest).toHaveBeenCalled();
        });

        expect(mockGetGamesRequest).toHaveBeenCalledWith(
            '/game/genre?pageNum=2&pageSize=10',
            expect.any(Function),
            expect.any(Function)
        );
    });

    it('should calls getGamesRequest with correct URL for "search" mode', async () => {
        render(<GameListPage mode="search" />);

        await waitFor(() => {
            expect(mockGetGamesRequest).toHaveBeenCalled();
        });

        expect(mockGetGamesRequest).toHaveBeenCalledWith(
            '/game/name/testGame?pageNum=2&pageSize=10',
            expect.any(Function),
            expect.any(Function)
        );
    });

    it('should navigates to "/error" when getGamesRequest throws an error', async () => {
        mockGetGamesRequest.mockRejectedValue(new Error('Fetch error'));
        render(<GameListPage mode="list" />);

        await waitFor(() => {
            expect(mockGetGamesRequest).toHaveBeenCalled();
        });

        await waitFor(() => {
            expect(mockNavigate).toHaveBeenCalledWith('/error');
        });
    });

    it('should reloads page on popstate event', () => {
        const originalLocation = window.location;
        delete window.location;
        window.location = { ...originalLocation, reload: jest.fn() };

        render(<GameListPage mode="list" />);

        act(() => {
            window.dispatchEvent(new Event('popstate'));
        });

        expect(window.location.reload).toHaveBeenCalled();

        window.location = originalLocation;
    });
});
