// useHttpHelper.test.js
import { renderHook, act } from '@testing-library/react-hooks';
import { useHttpHelper } from './useHttpHelper';

// Mock dependencies before importing the hook.
// For AuthContext, we simply return fixed tokens.
jest.mock('@contexts/AuthContext', () => ({
    useAuth: () => ({
        getAccessToken: () => 'access-token',
        getLinkToken: () => 'link-token',
    }),
}));

// For NavigationContext, define a mock inside the factory and export it.
jest.mock('@contexts/NavigationContext', () => {
    const mockNavigate = jest.fn();
    return {
        useNavigation: () => mockNavigate,
        __esModule: true,
        _mockNavigate: mockNavigate, // Exported for test assertions.
    };
});

// For the HTTP service, mock the request function.
jest.mock('@services/httpService', () => ({
    request: jest.fn(),
}));

// Import the request mock and the exported navigation mock.
import { request } from '@services/httpService';
import { _mockNavigate as navigateMock } from '@contexts/NavigationContext';

describe('useHttpHelper', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('handleRequest calls onSuccess when request succeeds', async () => {
        const response = { data: 'success' };
        request.mockResolvedValue(response);
        const onSuccess = jest.fn();
        const onError = jest.fn();

        const { result } = renderHook(() => useHttpHelper());

        await act(async () => {
            await result.current.handleRequest('GET', '/test', { foo: 'bar' }, onSuccess, onError);
        });

        // Verify that request() is called with the tokens returned by our mocks.
        expect(request).toHaveBeenCalledWith('GET', '/test', { foo: 'bar' }, 'access-token', 'link-token');
        expect(onSuccess).toHaveBeenCalledWith(response);
        expect(onError).not.toHaveBeenCalled();
        expect(navigateMock).not.toHaveBeenCalled();
    });

    test('handleRequest calls onError when request fails with a client error', async () => {
        const error = { response: { status: 400, data: 'client error' } };
        request.mockRejectedValue(error);
        const onSuccess = jest.fn();
        const onError = jest.fn();

        const { result } = renderHook(() => useHttpHelper());

        await act(async () => {
            await result.current.handleRequest('POST', '/test', { foo: 'bar' }, onSuccess, onError);
        });

        expect(onError).toHaveBeenCalledWith('client error');
        expect(navigateMock).not.toHaveBeenCalled();
    });

    test('handleRequest navigates to /error when request fails with a server error', async () => {
        const error = { response: { status: 500, data: 'server error' } };
        request.mockRejectedValue(error);
        const onSuccess = jest.fn();
        const onError = jest.fn();

        const { result } = renderHook(() => useHttpHelper());

        await act(async () => {
            await result.current.handleRequest('PUT', '/test', { foo: 'bar' }, onSuccess, onError);
        });

        expect(navigateMock).toHaveBeenCalledWith('/error', { state: { errorMessage: 'server error' } });
        expect(onError).not.toHaveBeenCalled();
    });
});
