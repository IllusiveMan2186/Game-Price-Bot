import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import Messenger from './Messenger';
import { useLinkActions } from '@hooks/user/useLinkActions';
import { useAuth } from '@contexts/AuthContext';

jest.mock('@util/message', () => ({
    __esModule: true,
    default: ({ string }) => <span>{string}</span>,
}));

jest.mock('@hooks/user/useLinkActions', () => ({
    useLinkActions: jest.fn()
}));

jest.mock('@contexts/AuthContext', () => ({
    useAuth: jest.fn()
}));

jest.mock('@assets/images/telegram.png', () => 'telegram.png');

describe('Messenger Component', () => {
    const mockGetLinkTokenForMessengerRequest = jest.fn();
    const mockIsUserAuth = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
        window.open = jest.fn();

        useLinkActions.mockReturnValue({
            getLinkTokenForMessengerRequest: mockGetLinkTokenForMessengerRequest
        });

        useAuth.mockReturnValue({
            isUserAuth: true // or false depending on the test
        });
    });

    it('should render the title and Telegram button with icon', () => {
        render(<Messenger />);

        expect(screen.getByText('app.footer.messenger')).toBeInTheDocument();
        const button = screen.getByRole('button');
        expect(button).toBeInTheDocument();
        expect(screen.getByAltText('Telegram Icon')).toBeInTheDocument();
    });

    it('should call getLinkTokenForMessengerRequest when user is authenticated and clicks the button', () => {
        useAuth.mockReturnValue({
            isUserAuth: true
        });
        render(<Messenger />);

        const button = screen.getByRole('button');
        fireEvent.click(button);

        expect(mockGetLinkTokenForMessengerRequest).toHaveBeenCalledWith(
            'https://t.me/GamaPriceTelegramBot?start='
        );
    });

    it('should open a new tab with the Telegram link when user is not authenticated', () => {
        useAuth.mockReturnValue({
            isUserAuth: false
        });
        const originalOpen = window.open;
        window.open = jest.fn();

        render(<Messenger />);

        const button = screen.getByRole('button');
        fireEvent.click(button);

        expect(window.open).toHaveBeenCalledWith(
            'https://t.me/GamaPriceTelegramBot',
            '_blank'
        );

        window.open = originalOpen;
    });
});
