import React from 'react';
import { render, screen } from '@testing-library/react';
import GameListLoader from './GameListLoader';

// Mock child components to simplify tests
jest.mock('@components/game/list/loader/list/GameList', () => () => <div data-testid="game-list" />);
jest.mock('@components/game/list/loader/pagination/Pagination', () => () => <div data-testid="pagination" />);
jest.mock('@components/game/shared/loading/Loading', () => () => <div data-testid="loading" />);
jest.mock('@util/message', () => ({ string }) => <div data-testid="message">{string}</div>);

describe('GameListLoader', () => {
  const defaultProps = {
    elementAmount: 100,
    page: 1,
    mode: 'normal',
    updateSearchParams: jest.fn(),
    pageSize: 10,
    reloadPage: jest.fn(),
  };

  test('renders Loading when games is undefined and mode is "search"', () => {
    render(<GameListLoader {...defaultProps} games={undefined} mode="search" />);
    expect(screen.getByTestId('loading')).toBeInTheDocument();
  });

  test('renders Message when games is undefined and mode is not "search"', () => {
    render(<GameListLoader {...defaultProps} games={undefined} mode="other" />);
    const message = screen.getByTestId('message');
    expect(message).toBeInTheDocument();
    expect(message).toHaveTextContent("app.game.error.name.not.found");
  });

  test('renders Message when games is an empty array', () => {
    render(<GameListLoader {...defaultProps} games={[]} />);
    const message = screen.getByTestId('message');
    expect(message).toBeInTheDocument();
    expect(message).toHaveTextContent("app.game.error.name.not.found");
  });

  test('renders GameList and Pagination when games is non-empty', () => {
    const games = [{ id: 1, name: 'Game1' }];
    render(<GameListLoader {...defaultProps} games={games} />);
    expect(screen.getByTestId('game-list')).toBeInTheDocument();
    expect(screen.getByTestId('pagination')).toBeInTheDocument();
  });
});
