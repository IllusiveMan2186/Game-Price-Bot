import React from 'react';
import { render, waitFor, screen, cleanup, act } from '@testing-library/react';
import GameListPage from './GameListPage';
import { useParams } from 'react-router-dom';
import { useNavigation } from '@contexts/NavigationContext';
import { useGameActions } from '@hooks/game/useGameActions';

// Mock dependencies
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

// Mock child components
jest.mock('@components/game/list/filter/GameListFilter', () => () => (
  <div data-testid="game-list-filter" />
));
jest.mock('@components/game/list/loader/GameListLoader', () => () => (
  <div data-testid="game-list-loader" />
));
jest.mock('@components/game/list/header/GameListPageHeader', () => () => (
  <div data-testid="game-list-page-header" />
));

// Mock constants used in the component
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
    // Make sure the API call resolves by default
    mockGetGamesRequest.mockResolvedValue();
  });

  afterEach(() => {
    cleanup();
  });

  test('renders GameListFilter, GameListPageHeader, and GameListLoader in "list" mode', async () => {
    render(<GameListPage mode="list" />);
    
    // Wait for the effect calling getGamesRequest to complete
    await waitFor(() => {
      expect(mockGetGamesRequest).toHaveBeenCalled();
    });

    expect(screen.getByTestId('game-list-filter')).toBeInTheDocument();
    expect(screen.getByTestId('game-list-page-header')).toBeInTheDocument();
    expect(screen.getByTestId('game-list-loader')).toBeInTheDocument();
  });

  test('does not render GameListFilter when mode is not "list"', async () => {
    render(<GameListPage mode="search" />);
    
    await waitFor(() => {
      expect(mockGetGamesRequest).toHaveBeenCalled();
    });

    expect(screen.queryByTestId('game-list-filter')).toBeNull();
    expect(screen.getByTestId('game-list-page-header')).toBeInTheDocument();
    expect(screen.getByTestId('game-list-loader')).toBeInTheDocument();
  });

  test('calls getGamesRequest with correct URL for "list" mode', async () => {
    render(<GameListPage mode="list" />);
    
    await waitFor(() => {
      expect(mockGetGamesRequest).toHaveBeenCalled();
    });
    
    // In "list" mode, getSearchParametrs returns:
    // "/game/genre?" + searchParams.toString()
    // Given useParams.url = '?pageNum=2&pageSize=10'
    expect(mockGetGamesRequest).toHaveBeenCalledWith(
      '/game/genre?pageNum=2&pageSize=10',
      expect.any(Function),
      expect.any(Function)
    );
  });

  test('calls getGamesRequest with correct URL for "search" mode', async () => {
    render(<GameListPage mode="search" />);
    
    await waitFor(() => {
      expect(mockGetGamesRequest).toHaveBeenCalled();
    });
    
    // In "search" mode, getSearchParametrs returns:
    // "/game/name/" + name + "?" + searchParams.toString()
    // where name is initialized from useParams.searchName ("testGame")
    expect(mockGetGamesRequest).toHaveBeenCalledWith(
      '/game/name/testGame?pageNum=2&pageSize=10',
      expect.any(Function),
      expect.any(Function)
    );
  });

  test('navigates to "/error" when getGamesRequest throws an error', async () => {
    // Force getGamesRequest to reject
    mockGetGamesRequest.mockRejectedValue(new Error('Fetch error'));
    render(<GameListPage mode="list" />);
    
    await waitFor(() => {
      expect(mockGetGamesRequest).toHaveBeenCalled();
    });
    
    // The error is caught and navigate is called with '/error'
    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/error');
    });
  });

  test('reloads page on popstate event', () => {
    // Save the original location object
    const originalLocation = window.location;
    // Delete window.location to override it
    delete window.location;
    window.location = { ...originalLocation, reload: jest.fn() };

    render(<GameListPage mode="list" />);
    
    // Dispatch a popstate event to trigger the listener
    act(() => {
      window.dispatchEvent(new Event('popstate'));
    });
    
    expect(window.location.reload).toHaveBeenCalled();

    // Restore original window.location if necessary
    window.location = originalLocation;
  });
});
