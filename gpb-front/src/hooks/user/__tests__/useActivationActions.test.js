import { renderHook } from '@testing-library/react';
import { useActivationActions } from '../useActivationActions';
import { useHttpHelper } from '@hooks/useHttpHelper';
import { useNavigation } from '@contexts/NavigationContext';

jest.mock('@hooks/useHttpHelper');
jest.mock('@contexts/NavigationContext');

describe('useActivationActions', () => {
    const navigateMock = jest.fn();
    const handleRequestMock = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
        useNavigation.mockReturnValue(navigateMock);
        useHttpHelper.mockReturnValue({ handleRequest: handleRequestMock });
    });

    test('activateUserAccountRequest calls handleRequest correctly and navigates on success', () => {
        const { result } = renderHook(() => useActivationActions());

        const token = 'activation-token';
        result.current.activateUserAccountRequest(token);

        expect(handleRequestMock).toHaveBeenCalledWith(
            'POST',
            undefined, // API_ENDPOINTS.ACTIVATE_USER is not defined; ensure it is defined in the original file
            { token },
            expect.any(Function),
            expect.any(Function)
        );

        // Simulate successful response callback
        handleRequestMock.mock.calls[0][3]();
        expect(navigateMock).toHaveBeenCalledWith('/login');
    });

    test('resendActivationEmailRequest calls handleRequest correctly and navigates on success', () => {
        const { result } = renderHook(() => useActivationActions());

        const email = 'user@example.com';
        result.current.resendActivationEmailRequest(email);

        expect(handleRequestMock).toHaveBeenCalledWith(
            'POST',
            '/email/resend',
            { email },
            expect.any(Function),
            expect.any(Function)
        );

        // Simulate successful response callback
        handleRequestMock.mock.calls[0][3]();
        expect(navigateMock).toHaveBeenCalledWith('/login');
    });
});
