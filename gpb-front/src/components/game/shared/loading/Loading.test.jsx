import React from 'react';
import { render, screen } from '@testing-library/react';
import Loading from './Loading';

// Mock the Message component with proper ES Module interop
jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <div data-testid="message">{string}</div>
}));

describe('Loading', () => {
  test('renders message and loading image correctly', () => {
    render(<Loading />);

    // Verify that the Message component is rendered with the expected string
    const message = screen.getByTestId('message');
    expect(message).toHaveTextContent('app.game.search.wait');

    // Verify that the image is rendered with the correct class and src attribute
    const img = screen.getByRole('img');
    expect(img).toHaveClass('loading-img');
    expect(img).toHaveAttribute('src', '/assets/images/load.png');

    // Optionally, verify the container has the correct className
    const container = img.closest('.app-content-loading');
    expect(container).toBeInTheDocument();
  });
});
