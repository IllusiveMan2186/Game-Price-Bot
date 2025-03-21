// useGameActions.test.js
import { renderHook, act } from '@testing-library/react-hooks';
import { useGameActions } from '../useGameActions'; // Adjust the path as needed

// --- Mock the Navigation Context ---
// We define a factory that returns a new jest function, and export it as _mockNavigate.
jest.mock('@contexts/NavigationContext', () => {
    const mockNavigate = jest.fn();
    return {
        useNavigation: () => mockNavigate,
        __esModule: true,
        _mockNavigate: mockNavigate,
    };
});

// --- Mock the HTTP Helper ---
// We define a factory that creates an internal mock for handleRequest and export it.
jest.mock('@hooks/useHttpHelper', () => {
    const handleRequestMock = jest.fn();
    return {
        useHttpHelper: () => ({ handleRequest: handleRequestMock }),
        __esModule: true,
        _handleRequestMock: handleRequestMock,
    };
});

// Now import our internal mocks from the mocked modules.
import { _mockNavigate as navigateMock } from '@contexts/NavigationContext';
import { _handleRequestMock as handleRequestMock } from '@hooks/useHttpHelper';

describe('useGameActions', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('getGamesRequest calls handleRequest with correct parameters', () => {
        const setElementAmount = jest.fn();
        const setGames = jest.fn();
        const searchParameters = '/user/games?foo=bar';

        const { result } = renderHook(() => useGameActions());

        act(() => {
            result.current.getGamesRequest(searchParameters, setElementAmount, setGames);
        });

        // Verify that handleRequest was called with method "GET", the given URL, no data,
        // and a success callback.
        expect(handleRequestMock).toHaveBeenCalledWith(
            'GET',
            searchParameters,
            null,
            expect.any(Function)
        );

        // Simulate the success callback with a response.
        const onSuccessCallback = handleRequestMock.mock.calls[0][3];
        const responseData = { elementAmount: 10, games: [{ id: 1 }, { id: 2 }] };
        onSuccessCallback({ data: responseData });

        expect(setElementAmount).toHaveBeenCalledWith(10);
        expect(setGames).toHaveBeenCalledWith([{ id: 1 }, { id: 2 }]);
    });

    test('getGameRequest calls handleRequest with correct parameters and handles error', () => {
        const setGame = jest.fn();
        const onError = jest.fn();
        const gameId = '123';

        const { result } = renderHook(() => useGameActions());

        act(() => {
            result.current.getGameRequest(gameId, setGame, onError);
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            'GET',
            `/game/${gameId}`,
            null,
            expect.any(Function),
            expect.any(Function)
        );

        // Simulate success callback.
        const onSuccessCallback = handleRequestMock.mock.calls[0][3];
        onSuccessCallback({ data: { id: '123', name: 'Test Game' } });
        expect(setGame).toHaveBeenCalledWith({ id: '123', name: 'Test Game' });

        // Simulate error callback.
        const onErrorCallback = handleRequestMock.mock.calls[0][4];
        onErrorCallback('error message');
        expect(onError).toHaveBeenCalledWith('error message');
    });

    test('getGameByUrlRequest calls handleRequest and navigates on success', () => {
        const url = 'game-url';

        const { result } = renderHook(() => useGameActions());

        act(() => {
            result.current.getGameByUrlRequest(url);
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            'GET',
            `/game/url?url=${url}`,
            null,
            expect.any(Function)
        );

        // Simulate success callback returning an object with id.
        const onSuccessCallback = handleRequestMock.mock.calls[0][3];
        onSuccessCallback({ data: { id: '456' } });
        expect(navigateMock).toHaveBeenCalledWith(`/game/456`);
    });

    test('removeGameRequest calls handleRequest and navigates after timeout', () => {
        jest.useFakeTimers();
        const gameId = '789';

        const { result } = renderHook(() => useGameActions());

        act(() => {
            result.current.removeGameRequest(gameId);
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            'DELETE',
            `/game/${gameId}`,
            {},
            expect.any(Function)
        );

        // Simulate the success callback that schedules navigation after 100ms.
        const onSuccessCallback = handleRequestMock.mock.calls[0][3];
        onSuccessCallback();

        act(() => {
            jest.advanceTimersByTime(100);
        });

        expect(navigateMock).toHaveBeenCalledWith('/');
        jest.useRealTimers();
    });
});
