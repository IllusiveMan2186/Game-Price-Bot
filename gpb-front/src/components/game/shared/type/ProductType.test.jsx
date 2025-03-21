import React from 'react';
import { render, screen } from '@testing-library/react';
import { ProductType } from './ProductType';

// Mock Message component with proper ES module interop
jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <div data-testid="message">{string}</div>
}));

describe('ProductType', () => {
  test('renders Message with correct string when type is provided', () => {
    const type = 'Action';
    render(<ProductType type={type} />);
    
    // Check that the container with the class "app-game-type" is rendered
    const container = document.querySelector('.app-game-type');
    expect(container).toBeInTheDocument();
    
    // Check that the Message component is rendered with the expected string
    const message = screen.getByTestId('message');
    expect(message).toHaveTextContent(`app.game.info.type.${type.toLowerCase()}`);
  });

  test('renders nothing when type is not provided', () => {
    const { container } = render(<ProductType />);
    // Expect the component to render null (no output)
    expect(container.firstChild).toBeNull();
  });
});
