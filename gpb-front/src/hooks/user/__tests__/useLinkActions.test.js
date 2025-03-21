// useLinkActions.test.js
import { renderHook, act } from '@testing-library/react';
import { useLinkActions } from '../useLinkActions';
import { useNavigation } from '@contexts/NavigationContext';
import { useHttpHelper } from '@hooks/useHttpHelper';
import { NotificationManager } from 'react-notifications';

jest.mock('@contexts/NavigationContext', () => ({
    useNavigation: jest.fn(),
}));

jest.mock('@hooks/useHttpHelper', () => ({
    useHttpHelper: jest.fn(),
}));

jest.mock('react-notifications', () => ({
    NotificationManager: {
        success: jest.fn(),
    },
}));

describe('useLinkActions', () => {
    const navigateMock = jest.fn();
    const handleRequestMock = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();

        useNavigation.mockReturnValue(navigateMock);
        useHttpHelper.mockReturnValue({ handleRequest: handleRequestMock });
    });

    test('accountLinkRequest should handle success', () => {
        const { result } = renderHook(() => useLinkActions());

        act(() => {
            result.current.accountLinkRequest('test-token');
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            "POST",
            "/linker",
            { token: 'test-token' },
            expect.any(Function)
        );

        // simulate successful callback
        const successCallback = handleRequestMock.mock.calls[0][3];
        successCallback();

        expect(NotificationManager.success).toHaveBeenCalled();
        expect(navigateMock).toHaveBeenCalledWith("/");
    });

    test('getLinkTokenRequest handles success response', () => {
        const setTokenMock = jest.fn();
        const { result } = renderHook(() => useLinkActions());

        act(() => {
            result.current.getLinkTokenRequest(setTokenMock);
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            "GET",
            "/linker",
            null,
            expect.any(Function),
            expect.any(Function)
        );

        const successCallback = handleRequestMock.mock.calls[0][3];
        successCallback({ data: 'link-token' });

        expect(setTokenMock).toHaveBeenCalledWith('link-token');
    });

    test('getLinkTokenForMessengerRequest should open URL on success', () => {
        window.open = jest.fn();
        const { result } = renderHook(() => useLinkActions());

        act(() => {
            result.current.getLinkTokenForMessengerRequest('https://messenger.com/?token=');
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            "GET",
            "/linker",
            null,
            expect.any(Function)
        );

        const successCallback = handleRequestMock.mock.calls[0][3];
        successCallback({ data: 'messenger-token' });

        expect(window.open).toHaveBeenCalledWith(
            'https://messenger.com/?token=messenger-token',
            '_blank'
        );
    });
});