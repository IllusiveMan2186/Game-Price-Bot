import React from 'react';
import { render, screen } from '@testing-library/react';
import { CommonGameInfo } from './CommonGameInfo';

// Mock the child components
jest.mock('@components/game/shared/type/ProductType', () => ({
  ProductType: ({ type }) => <div data-testid="product-type">{type}</div>,
}));

jest.mock('@components/game/shared/availability/GameAvailability', () => ({
  GameAvailability: ({ available }) => (
    <div data-testid="game-availability">{available ? 'Available' : 'Not Available'}</div>
  ),
}));

describe('CommonGameInfo', () => {
  const game = {
    type: 'Action',
    available: true,
    minPrice: 50,
    maxPrice: 150,
  };

  const className = 'custom-game-info';

  test('renders product type, game availability, and price correctly', () => {
    render(<CommonGameInfo game={game} className={className} />);
    
    // Verify container has the provided className
    const container = screen.getByTestId('product-type').parentElement;
    expect(container).toHaveClass(className);
    
    // Check that the product type is rendered correctly
    expect(screen.getByTestId('product-type')).toHaveTextContent(game.type);
    
    // Check that the game availability is rendered correctly
    expect(screen.getByTestId('game-availability')).toHaveTextContent('Available');
    
    // Check that the price information is rendered correctly
    expect(screen.getByText(`${game.minPrice} - ${game.maxPrice} ₴`)).toBeInTheDocument();
  });

  test('renders "Not Available" when game is not available', () => {
    render(<CommonGameInfo game={{ ...game, available: false }} className={className} />);
    expect(screen.getByTestId('game-availability')).toHaveTextContent('Not Available');
  });
});
