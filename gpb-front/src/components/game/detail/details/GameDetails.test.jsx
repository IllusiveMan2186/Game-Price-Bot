import { render, screen } from '@testing-library/react';
import GameDetails from './GameDetails';
import { useAuth } from '@contexts/AuthContext';

jest.mock('@contexts/AuthContext', () => ({
  useAuth: jest.fn(),
}));

describe('GameDetails Component', () => {
  const mockGame = {
    name: 'Sample Game',
    genres: ['Action', 'Adventure'],
    userSubscribed: false,
    gamesInShop: [],
  };

  const mockGameId = '1';
  const mockNavigate = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders game details correctly for admin users', () => {
    useAuth.mockReturnValue({
      isUserAdmin: () => true,
      isUserAuth: () => true,
    });

    render(<GameDetails game={mockGame} gameId={mockGameId} navigate={mockNavigate} />);
    expect(screen.getByText('Sample Game')).toBeInTheDocument();
  });

  test('renders game details correctly for non-admin users', () => {
    useAuth.mockReturnValue({
      isUserAdmin: () => false,
      isUserAuth: () => false,
    });

    render(<GameDetails game={mockGame} gameId={mockGameId} navigate={mockNavigate} />);
    expect(screen.getByText('Sample Game')).toBeInTheDocument();
  });
});
