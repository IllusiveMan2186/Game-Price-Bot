import React from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import GameList from './GameList';
import { useNavigation } from '@contexts/NavigationContext';

jest.mock('@contexts/NavigationContext', () => ({
    useNavigation: jest.fn(),
}));

describe('GameList Component', () => {
    const mockNavigate = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
        useNavigation.mockReturnValue(mockNavigate);
    });

    it('should render "No games available" when games prop is not an array', () => {
        render(<GameList games={null} />);
        expect(screen.getByText('No games available')).toBeInTheDocument();
    });

    it('should render the correct number of game items', () => {
        const games = [
            { id: 1, name: 'Game 1' },
            { id: 2, name: 'Game 2' },
        ];
        render(<GameList games={games} />);

        expect(screen.getByText('Game 1')).toBeInTheDocument();
        expect(screen.getByText('Game 2')).toBeInTheDocument();
    });

    it('should navigate to the correct URL when a game is clicked', () => {
        const games = [{ id: 1, name: 'Game 1' }];
        render(<GameList games={games} />);
        const gameItem = screen.getByText('Game 1');
        userEvent.click(gameItem);
        expect(mockNavigate).toHaveBeenCalledWith('/game/1');
    });
});
