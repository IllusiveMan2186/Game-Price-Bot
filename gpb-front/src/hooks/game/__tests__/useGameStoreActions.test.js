import { renderHook, act } from '@testing-library/react';
import { useGameStoreActions } from '../useGameStoreActions';

jest.mock('@hooks/useHttpHelper', () => ({
    useHttpHelper: jest.fn(),
}));

jest.mock('@contexts/NavigationContext', () => ({
    useNavigation: jest.fn(),
}));

import { useHttpHelper } from '@hooks/useHttpHelper';
import { useNavigation } from '@contexts/NavigationContext';

describe('useGameStoreActions', () => {
    const handleRequestMock = jest.fn();
    const navigateMock = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();

        useHttpHelper.mockReturnValue({ handleRequest: handleRequestMock });
        useNavigation.mockReturnValue(navigateMock);
    });

    it('should removeGameInStoreRequest calls handleRequest with correct params', () => {
        const onSuccessMock = jest.fn();
        const gameInStoreId = 123;

        const { result } = renderHook(() => useGameStoreActions());

        act(() => {
            result.current.removeGameInStoreRequest(gameInStoreId, onSuccessMock);
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            'DELETE',
            `/game/store/${gameInStoreId}`,
            {},
            expect.any(Function)
        );

        handleRequestMock.mock.calls[0][3]();
        expect(onSuccessMock).toHaveBeenCalled();
    });

    it('should addGameInStoreByUrlRequest calls handleRequest with correct params', () => {
        const gameId = 456;
        const url = 'http://example.com/game';

        const { result } = renderHook(() => useGameStoreActions());

        act(() => {
            result.current.addGameInStoreByUrlRequest(gameId, url);
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            'POST',
            '/game/url',
            { gameId, url },
            expect.any(Function)
        );
    });
});
