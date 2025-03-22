import React from 'react';
import { render, screen } from '@testing-library/react';
import { CommonGameInfo } from './CommonGameInfo';

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

  it('should renders product type, game availability, and price correctly', () => {
    render(<CommonGameInfo game={game} className={className} />);
    
    const container = screen.getByTestId('product-type').parentElement;
    expect(container).toHaveClass(className);
    
    expect(screen.getByTestId('product-type')).toHaveTextContent(game.type);
    
    expect(screen.getByTestId('game-availability')).toHaveTextContent('Available');
    
    expect(screen.getByText(`${game.minPrice} - ${game.maxPrice} ₴`)).toBeInTheDocument();
  });

  it('should renders "Not Available" when game is not available', () => {
    render(<CommonGameInfo game={{ ...game, available: false }} className={className} />);
    expect(screen.getByTestId('game-availability')).toHaveTextContent('Not Available');
  });
});
