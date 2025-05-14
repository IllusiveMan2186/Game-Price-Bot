import { renderHook, act } from '@testing-library/react-hooks';
import { useHttpHelper } from './useHttpHelper';

jest.mock('@contexts/NavigationContext', () => {
    const mockNavigate = jest.fn();
    return {
        useNavigation: () => mockNavigate,
        __esModule: true,
        _mockNavigate: mockNavigate,
    };
});

jest.mock('@services/httpService', () => ({
    request: jest.fn(),
}));

import { request } from '@services/httpService';
import { _mockNavigate as navigateMock } from '@contexts/NavigationContext';

describe('useHttpHelper', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('should handleRequest calls onSuccess when request succeeds', async () => {
        const response = { data: 'success' };
        request.mockResolvedValue(response);
        const onSuccess = jest.fn();
        const onError = jest.fn();

        const { result } = renderHook(() => useHttpHelper());

        await act(async () => {
            await result.current.handleRequest('GET', '/test', { foo: 'bar' }, onSuccess, onError);
        });

        expect(request).toHaveBeenCalledWith('GET', '/test', { foo: 'bar' });
        expect(onSuccess).toHaveBeenCalledWith(response);
        expect(onError).not.toHaveBeenCalled();
        expect(navigateMock).not.toHaveBeenCalled();
    });

    it('should handleRequest calls onError when request fails with a client error', async () => {
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

    it('should handleRequest navigates to /error when request fails with a server error', async () => {
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
