import { renderHook, act } from '@testing-library/react-hooks';
import { useGameActions } from '../useGameActions';

jest.mock('@contexts/NavigationContext', () => {
    const mockNavigate = jest.fn();
    return {
        useNavigation: () => mockNavigate,
        __esModule: true,
        _mockNavigate: mockNavigate,
    };
});

jest.mock('@hooks/useHttpHelper', () => {
    const handleRequestMock = jest.fn();
    return {
        useHttpHelper: () => ({ handleRequest: handleRequestMock }),
        __esModule: true,
        _handleRequestMock: handleRequestMock,
    };
});

import { _mockNavigate as navigateMock } from '@contexts/NavigationContext';
import { _handleRequestMock as handleRequestMock } from '@hooks/useHttpHelper';

describe('useGameActions', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('should getGamesRequest calls handleRequest with correct parameters', () => {
        const setElementAmount = jest.fn();
        const setGames = jest.fn();
        const searchParameters = '/user/games?foo=bar';

        const { result } = renderHook(() => useGameActions());

        act(() => {
            result.current.getGamesRequest(searchParameters, setElementAmount, setGames);
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            'GET',
            searchParameters,
            null,
            expect.any(Function)
        );

        const onSuccessCallback = handleRequestMock.mock.calls[0][3];
        const responseData = { elementAmount: 10, games: [{ id: 1 }, { id: 2 }] };
        onSuccessCallback({ data: responseData });

        expect(setElementAmount).toHaveBeenCalledWith(10);
        expect(setGames).toHaveBeenCalledWith([{ id: 1 }, { id: 2 }]);
    });

    it('should getGameRequest calls handleRequest with correct parameters and handles error', () => {
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

        const onSuccessCallback = handleRequestMock.mock.calls[0][3];
        onSuccessCallback({ data: { id: '123', name: 'Test Game' } });
        expect(setGame).toHaveBeenCalledWith({ id: '123', name: 'Test Game' });

        const onErrorCallback = handleRequestMock.mock.calls[0][4];
        onErrorCallback('error message');
        expect(onError).toHaveBeenCalledWith('error message');
    });

    it('should getGameByUrlRequest calls handleRequest and navigates on success', () => {
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

        const onSuccessCallback = handleRequestMock.mock.calls[0][3];
        onSuccessCallback({ data: { id: '456' } });
        expect(navigateMock).toHaveBeenCalledWith(`/game/456`);
    });

    it('should removeGameRequest calls handleRequest and navigates after timeout', () => {
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

        const onSuccessCallback = handleRequestMock.mock.calls[0][3];
        onSuccessCallback();

        act(() => {
            jest.advanceTimersByTime(100);
        });

        expect(navigateMock).toHaveBeenCalledWith('/');
        jest.useRealTimers();
    });
});
