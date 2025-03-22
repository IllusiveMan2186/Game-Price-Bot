import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import GameDetailPage from './GameDetailPage';
import { useParams } from 'react-router-dom';
import { useNavigation } from '@contexts/NavigationContext';
import { useGameActions } from '@hooks/game/useGameActions';

jest.mock('react-router-dom', () => ({
    useParams: jest.fn(),
}));

jest.mock('@contexts/NavigationContext', () => ({
    useNavigation: jest.fn(),
}));

jest.mock('@hooks/game/useGameActions', () => ({
    useGameActions: jest.fn(),
}));

jest.mock('@util/message', () => ({
    __esModule: true,
    default: ({ string }) => <span>{string}</span>,
}));

jest.mock('@components/game/shared/loading/Loading', () => () => <div>Loading...</div>);

jest.mock('@components/game/detail/details/GameDetails', () => () => <div>Sample Game</div>);

jest.mock('@components/game/shared/image/GameImage', () => () => <div>GameImage</div>);

describe('GameDetailPage Component', () => {
    const mockNavigate = jest.fn();
    const mockGetGameRequest = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
        useParams.mockReturnValue({ gameId: '1' });
        useNavigation.mockReturnValue(mockNavigate);
        useGameActions.mockReturnValue({
            getGameRequest: mockGetGameRequest,
        });
    });

    it('renders loading state initially', () => {
        render(<GameDetailPage />);
        expect(screen.getByText('Loading...')).toBeInTheDocument();
    });

    it('renders game details after successful fetch', async () => {
        const mockGame = {
            name: 'Sample Game',
            genres: ['Action', 'Adventure'],
            userSubscribed: false,
            gamesInShop: [],
        };

        mockGetGameRequest.mockImplementationOnce(async (gameId, setGame) => {
            setGame(mockGame);
        });

        render(<GameDetailPage />);

        await waitFor(() => {
            expect(screen.getByText('Sample Game')).toBeInTheDocument();
        });
    });

    it('renders error message if fetch fails', async () => {
        const mockError = 'Error fetching game data';

        mockGetGameRequest.mockImplementationOnce(async (gameId, setGame, setError) => {
            setError(mockError);
        });

        render(<GameDetailPage />);

        await waitFor(() => {
            expect(screen.getByText(mockError)).toBeInTheDocument();
        });
    });
});
