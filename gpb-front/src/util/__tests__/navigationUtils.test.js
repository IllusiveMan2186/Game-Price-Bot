import { reloadPage } from '@util/navigationUtils';
import { buildSearchParams } from '@util/searchParamsUtils';

jest.mock('@util/searchParamsUtils', () => ({
    buildSearchParams: jest.fn(),
}));

describe('searchParamsUtils', () => {
    let navigate;

    beforeEach(() => {
        navigate = jest.fn();
        buildSearchParams.mockClear();
    });

    it('should navigates to /user/games/ when mode is usersGames', () => {
        buildSearchParams.mockReturnValue('foo=bar');
        const params = { mode: 'usersGames' };

        reloadPage(navigate, params);

        expect(buildSearchParams).toHaveBeenCalledWith(params);
        expect(navigate).toHaveBeenCalledWith('/user/games/foo=bar');
        expect(navigate).toHaveBeenCalledWith(0);
    });

    it('should navigates to /search/:term/ when mode is search', () => {
        buildSearchParams.mockReturnValue('a=1');
        const params = { mode: 'search', search: 'myTerm' };

        reloadPage(navigate, params);

        expect(buildSearchParams).toHaveBeenCalledWith(params);
        expect(navigate).toHaveBeenCalledWith('/search/myTerm/a=1');
        expect(navigate).toHaveBeenCalledWith(0);
    });

    it('should falls back to /games/ for any other mode', () => {
        buildSearchParams.mockReturnValue('');
        const params = { mode: 'list' };

        reloadPage(navigate, params);

        expect(buildSearchParams).toHaveBeenCalledWith(params);
        expect(navigate).toHaveBeenCalledWith('/games/');
        expect(navigate).toHaveBeenCalledWith(0);
    });
});
