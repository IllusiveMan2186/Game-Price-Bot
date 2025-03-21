// useEmailActions.test.js

import { renderHook, act } from '@testing-library/react';
import { useEmailActions } from '../useEmailActions';
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
        error: jest.fn(),
    },
}));

describe('useEmailActions', () => {
    const navigateMock = jest.fn();
    const handleRequestMock = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
        useNavigation.mockReturnValue(navigateMock);
        useHttpHelper.mockReturnValue({ handleRequest: handleRequestMock });
    });

    test('emailChangeRequest calls handleRequest with correct parameters', () => {
        const { result } = renderHook(() => useEmailActions());

        const setErrorMessage = jest.fn();

        act(() => {
            result.current.emailChangeRequest('test@example.com', setErrorMessage);
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            "PUT",
            '/email',
            { email: 'test@example.com' },
            expect.any(Function),
            setErrorMessage
        );

        const successCallback = handleRequestMock.mock.calls[0][3];
        act(() => successCallback({}));

        expect(NotificationManager.success).toHaveBeenCalled();
        expect(navigateMock).toHaveBeenCalledWith("/");
    });

    test('emailConfirmRequest calls handleRequest and handles success', () => {
        const { result } = renderHook(() => useEmailActions());

        act(() => {
            result.current.emailConfirmRequest('confirmation-token');
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            "POST",
            '/email/change/confirm',
            { token: 'confirmation-token' },
            expect.any(Function),
            expect.any(Function)
        );

        const successCallback = handleRequestMock.mock.calls[0][3];
        const response = { data: 'confirmation success message' };
        act(() => successCallback(response));

        expect(NotificationManager.success).toHaveBeenCalled();
        expect(navigateMock).toHaveBeenCalledWith("/");
    });

    test('emailConfirmRequest handles error correctly', () => {
        const { result } = renderHook(() => useEmailActions());

        act(() => {
            result.current.emailConfirmRequest('invalid-token');
        });

        const errorCallback = handleRequestMock.mock.calls[0][4];
        const error = 'Invalid token error';

        act(() => errorCallback(error));

        expect(NotificationManager.error).toHaveBeenCalled();
        expect(navigateMock).toHaveBeenCalledWith("/");
    });
});