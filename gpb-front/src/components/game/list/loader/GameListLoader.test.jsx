import { render, screen } from '@testing-library/react';
import { useSelector } from 'react-redux';

jest.mock('react-redux', () => ({
    useSelector: jest.fn(),
}));

jest.mock('@components/game/list/loader/list/GameList', () => () => <div data-testid="game-list" />);
jest.mock('@components/game/list/loader/pagination/Pagination', () => () => <div data-testid="pagination" />);
jest.mock('@components/game/shared/loading/Loading', () => () => <div data-testid="loading" />);
jest.mock('@util/message', () => ({ __esModule: true, default: ({ string }) => <div data-testid="message">{string}</div> }));

import GameListLoader from './GameListLoader';

describe('GameListLoader', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('should renders Loading when games is empty and mode is "search"', () => {
        useSelector.mockReturnValue({ games: [], mode: 'search' });
        render(<GameListLoader />);
        expect(screen.getByTestId('loading')).toBeInTheDocument();
    });

    it('should renders Message when games is empty and mode is not "search"', () => {
        useSelector.mockReturnValue({ games: [], mode: 'other' });
        render(<GameListLoader />);
        const msg = screen.getByTestId('message');
        expect(msg).toBeInTheDocument();
        expect(msg).toHaveTextContent('app.game.error.name.not.found');
    });

    it('should renders GameList and Pagination when games is non-empty', () => {
        useSelector.mockReturnValue({ games: [{ id: 1, name: 'Test' }], mode: 'list' });
        render(<GameListLoader />);
        expect(screen.getByTestId('game-list')).toBeInTheDocument();
        expect(screen.getByTestId('pagination')).toBeInTheDocument();
    });
});
