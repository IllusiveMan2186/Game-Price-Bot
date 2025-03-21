import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import SubscribeButton from './SubscribeButton';
import { useAuth } from '@contexts/AuthContext';
import { useGameSubscription } from '@hooks/game/useGameSubscription';

jest.mock('@contexts/AuthContext', () => ({
    useAuth: jest.fn(),
}));

jest.mock('@hooks/game/useGameSubscription', () => ({
    useGameSubscription: () => ({
        subscribeForGameRequest: jest.fn(),
        unsubscribeForGameRequest: jest.fn(),
    }),
}));

jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key) => key,
    }),
}));

jest.mock('@hooks/game/useGameSubscription', () => ({
    useGameSubscription: jest.fn(),
}));


describe('SubscribeButton Component', () => {
    const mockSubscribeForGameRequest = jest.fn();
    const mockUnsubscribeForGameRequest = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();

        useAuth.mockReturnValue({
            isUserAuth: () => true,
        });

        useGameSubscription.mockReturnValue({
            subscribeForGameRequest: mockSubscribeForGameRequest,
            unsubscribeForGameRequest: mockUnsubscribeForGameRequest,
        });
    });

    const renderComponent = (isUserAuth, isSubscribed) => {
        useAuth.mockReturnValue({
            isUserAuth: jest.fn().mockReturnValue(isUserAuth),
        });

        render(<SubscribeButton isSubscribed={isSubscribed} gameId="1" />);
    };

    it('renders subscribe button enabled for authenticated users', () => {
        renderComponent(true, false);
        const button = screen.getByRole('button', { name: /subscribe/i });
        expect(button).toBeEnabled();
    });

    it('renders subscribe button disabled for unauthenticated users with prompt', () => {
        renderComponent(false, false);
        const button = screen.getByRole('button', { name: /subscribe/i });
        expect(button).toBeDisabled();
        expect(screen.getByText(/need.auth/i)).toBeInTheDocument();
    });

    it('displays "Unsubscribe" when user is already subscribed', () => {
        renderComponent(true, true);
        const button = screen.getByRole('button', { name: /unsubscribe/i });
        expect(button).toBeInTheDocument();
    });

    it('calls subscribeForGameRequest on click when not subscribed', () => {
        renderComponent(true, false);
        const button = screen.getByRole('button', { name: /subscribe/i });
        fireEvent.click(button);
        expect(mockSubscribeForGameRequest).toHaveBeenCalledWith("1");
    });

    it('calls unsubscribeForGameRequest on click when subscribed', () => {
        renderComponent(true, true);
        const button = screen.getByRole('button', { name: /unsubscribe/i });
        fireEvent.click(button);
        expect(mockUnsubscribeForGameRequest).toHaveBeenCalledWith("1");
    });

    it('toggles button text upon subscription action', () => {
        const { rerender } = render(<SubscribeButton isSubscribed={false} gameId="1" />);

        // should initially render Subscribe button
        let button = screen.getByRole('button', { name: /subscribe/i });
        expect(button).toBeInTheDocument();

        // simulate user click (subscribe)
        fireEvent.click(button);

        // rerender with updated prop to simulate state change
        rerender(<SubscribeButton isSubscribed={true} gameId="1" />);

        button = screen.getByRole('button', { name: /unsubscribe/i });
        expect(button).toBeInTheDocument();
    });
});
