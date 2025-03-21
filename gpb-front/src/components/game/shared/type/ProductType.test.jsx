import React from 'react';
import { render, screen } from '@testing-library/react';
import { ProductType } from './ProductType';

jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <div data-testid="message">{string}</div>
}));

describe('ProductType', () => {
  it('should renders Message with correct string when type is provided', () => {
    const type = 'Action';
    render(<ProductType type={type} />);
    
    const container = document.querySelector('.app-game-type');
    expect(container).toBeInTheDocument();
    
    const message = screen.getByTestId('message');
    expect(message).toHaveTextContent(`app.game.info.type.${type.toLowerCase()}`);
  });

  it('should renders nothing when type is not provided', () => {
    const { container } = render(<ProductType />);
    expect(container.firstChild).toBeNull();
  });
});
