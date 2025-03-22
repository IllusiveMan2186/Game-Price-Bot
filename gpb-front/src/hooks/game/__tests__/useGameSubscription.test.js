import { renderHook, act } from '@testing-library/react';
import { useGameSubscription } from '../useGameSubscription';

jest.mock('@hooks/useHttpHelper', () => ({
    useHttpHelper: jest.fn(),
}));

import { useHttpHelper } from '@hooks/useHttpHelper';

describe('useGameSubscription', () => {
    const handleRequestMock = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
        useHttpHelper.mockReturnValue({ handleRequest: handleRequestMock });
    });

    it('should subscribeForGameRequest calls handleRequest with correct parameters', () => {
        const gameId = 123;

        const { result } = renderHook(() => useGameSubscription());

        act(() => {
            result.current.subscribeForGameRequest(gameId);
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            'POST',
            `/user/games/${gameId}`,
            {},
            expect.any(Function)
        );
    });

    it('should unsubscribeForGameRequest calls handleRequest with correct parameters', () => {
        const gameId = 456;

        const { result } = renderHook(() => useGameSubscription());

        act(() => {
            result.current.unsubscribeForGameRequest(gameId);
        });

        expect(handleRequestMock).toHaveBeenCalledWith(
            'DELETE',
            `/user/games/${gameId}`,
            {},
            expect.any(Function)
        );
    });
});