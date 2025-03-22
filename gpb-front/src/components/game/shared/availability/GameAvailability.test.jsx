import React from 'react';
import { render, screen } from '@testing-library/react';
import { GameAvailability } from './GameAvailability';

jest.mock('@util/message', () => ({
    __esModule: true,
    default: ({ string }) => <span data-testid="message">{string}</span>
}));

describe('GameAvailability', () => {
    it('should renders available message with correct class', () => {
        render(<GameAvailability available={true} />);

        expect(screen.getByText('app.game.is.available')).toBeInTheDocument();

        const container = screen.getByTestId('message').closest('div');
        expect(container).toHaveClass('app-game-available');
        expect(container).not.toHaveClass('not-available');
    });

    it('should renders not available message with correct class', () => {
        render(<GameAvailability available={false} />);

        expect(screen.getByText('app.game.not.available')).toBeInTheDocument();

        const container = screen.getByTestId('message').closest('div');
        expect(container).toHaveClass('app-game-available');
        expect(container).toHaveClass('not-available');
    });
});
