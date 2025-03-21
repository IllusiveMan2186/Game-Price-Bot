import React from 'react';
import { render, screen } from '@testing-library/react';
import { GameAvailability } from './GameAvailability';

// Mock the Message component so that it renders the provided string within a span with a test id.
jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span data-testid="message">{string}</span>
}));

describe('GameAvailability', () => {
  test('renders available message with correct class', () => {
    render(<GameAvailability available={true} />);
    
    // Check that the correct message is rendered.
    expect(screen.getByText('app.game.is.available')).toBeInTheDocument();
    
    // Check that the container has the class "app-game-available" and does not include "not-available".
    const container = screen.getByTestId('message').closest('div');
    expect(container).toHaveClass('app-game-available');
    expect(container).not.toHaveClass('not-available');
  });

  test('renders not available message with correct class', () => {
    render(<GameAvailability available={false} />);
    
    // Check that the not available message is rendered.
    expect(screen.getByText('app.game.not.available')).toBeInTheDocument();
    
    // Verify that the container has both classes: "app-game-available" and "not-available".
    const container = screen.getByTestId('message').closest('div');
    expect(container).toHaveClass('app-game-available');
    expect(container).toHaveClass('not-available');
  });
});
