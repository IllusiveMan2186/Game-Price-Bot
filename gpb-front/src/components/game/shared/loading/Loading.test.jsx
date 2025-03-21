import React from 'react';
import { render, screen } from '@testing-library/react';
import Loading from './Loading';

jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <div data-testid="message">{string}</div>
}));

describe('Loading', () => {
  it('should renders message and loading image correctly', () => {
    render(<Loading />);

    const message = screen.getByTestId('message');
    expect(message).toHaveTextContent('app.game.search.wait');

    const img = screen.getByRole('img');
    expect(img).toHaveClass('loading-img');
    expect(img).toHaveAttribute('src', '/assets/images/load.png');

    const container = img.closest('.app-content-loading');
    expect(container).toBeInTheDocument();
  });
});
