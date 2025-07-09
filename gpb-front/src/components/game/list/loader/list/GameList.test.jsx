import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import GameList from './GameList';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { useNavigation } from '@contexts/NavigationContext';

jest.mock('@contexts/NavigationContext', () => ({
    useNavigation: jest.fn(),
}));

const mockStore = configureStore([]);

describe('GameList Component', () => {
    const mockNavigate = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
        useNavigation.mockReturnValue(mockNavigate);
    });

    it('should render "No games available" when games is not an array', () => {
        const store = mockStore({
            params: {
                games: null, // simulate invalid games
            },
        });

        render(
            <Provider store={store}>
                <GameList />
            </Provider>
        );

        expect(screen.getByText('No games available')).toBeInTheDocument();
    });

    it('should render the correct number of game items', () => {
        const store = mockStore({
            params: {
                games: [
                    { id: 1, name: 'Game 1' },
                    { id: 2, name: 'Game 2' },
                ],
            },
        });

        render(
            <Provider store={store}>
                <GameList />
            </Provider>
        );

        expect(screen.getByText('Game 1')).toBeInTheDocument();
        expect(screen.getByText('Game 2')).toBeInTheDocument();
    });

    it('should navigate to the correct URL when a game is clicked', () => {
        const store = mockStore({
            params: {
                games: [{ id: 1, name: 'Game 1' }],
            },
        });

        render(
            <Provider store={store}>
                <GameList />
            </Provider>
        );

        const gameItem = screen.getByText('Game 1');
        userEvent.click(gameItem);
        expect(mockNavigate).toHaveBeenCalledWith('/game/1');
    });
});
