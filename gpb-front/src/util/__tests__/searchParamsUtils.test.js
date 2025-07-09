import { buildSearchParams } from '@util/searchParamsUtils';
import * as constants from '@util/constants';

describe('searchParamsUtil', () => {
    const defaultSort = constants.sortsOptions[0].value;
    const defaultPageSize = constants.pageSizesOptions[0].value;

    function parse(qs) {
        return new URLSearchParams(qs);
    }

    it('should returns empty string when nothing is non-default and mode ≠ "list"', () => {
        const params = {
            mode: 'search',
            genres: ['A'],
            types: ['X'],
            sortBy: defaultSort,
            minPrice: 0,
            maxPrice: 10000,
            pageNum: 1,
            pageSize: defaultPageSize,
        };
        expect(buildSearchParams(params)).toBe('');
    });

    describe('buildSearchParams when mode === "list"', () => {
        it('should includes "genre" only if genres non-empty', () => {
            let p = { mode: 'list', genres: ['RPG'], types: [], sortBy: defaultSort, minPrice: 0, maxPrice: 10000, pageNum: 1, pageSize: defaultPageSize };
            let qs = parse(buildSearchParams(p));
            expect(qs.get('genre')).toBe('RPG');
            expect(qs.has('type')).toBe(false);

            p.genres = [];
            qs = parse(buildSearchParams(p));
            expect(qs.has('genre')).toBe(false);
        });

        it('should includes "type" only if types non-empty', () => {
            const p = { mode: 'list', genres: [], types: ['Action', 'Stealth'], sortBy: defaultSort, minPrice: 0, maxPrice: 10000, pageNum: 1, pageSize: defaultPageSize };
            const qs = parse(buildSearchParams(p));
            expect(qs.get('type')).toBe('Action,Stealth');
        });

        it('should includes minPrice only if ≠ 0', () => {
            let p = { mode: 'list', genres: [], types: [], sortBy: defaultSort, minPrice: 25, maxPrice: 10000, pageNum: 1, pageSize: defaultPageSize };
            let qs = parse(buildSearchParams(p));
            expect(qs.get('minPrice')).toBe('25');

            p.minPrice = 0;
            qs = parse(buildSearchParams(p));
            expect(qs.has('minPrice')).toBe(false);
        });

        it('should includes maxPrice only if ≠ 10000', () => {
            let p = { mode: 'list', genres: [], types: [], sortBy: defaultSort, minPrice: 0, maxPrice: 5000, pageNum: 1, pageSize: defaultPageSize };
            let qs = parse(buildSearchParams(p));
            expect(qs.get('maxPrice')).toBe('5000');

            p.maxPrice = 10000;
            qs = parse(buildSearchParams(p));
            expect(qs.has('maxPrice')).toBe(false);
        });
    });

    it('should includes sortBy only if non-default', () => {
        const nonDef = constants.sortsOptions.find(o => o.value !== defaultSort).value;
        let p = { mode: 'list', genres: [], types: [], sortBy: nonDef, minPrice: 0, maxPrice: 10000, pageNum: 1, pageSize: defaultPageSize };
        let qs = parse(buildSearchParams(p));
        expect(qs.get('sortBy')).toBe(nonDef);

        p.sortBy = defaultSort;
        qs = parse(buildSearchParams(p));
        expect(qs.has('sortBy')).toBe(false);
    });

    it('should includes pageSize only if non-default', () => {
        const nonDef = constants.pageSizesOptions.find(o => o.value !== defaultPageSize).value;
        let p = { mode: 'list', genres: [], types: [], sortBy: defaultSort, minPrice: 0, maxPrice: 10000, pageNum: 1, pageSize: nonDef };
        let qs = parse(buildSearchParams(p));
        expect(qs.get('pageSize')).toBe(String(nonDef));

        p.pageSize = defaultPageSize;
        qs = parse(buildSearchParams(p));
        expect(qs.has('pageSize')).toBe(false);
    });

    it('should includes pageNum only if ≠ 1', () => {
        let p = { mode: 'list', genres: [], types: [], sortBy: defaultSort, minPrice: 0, maxPrice: 10000, pageNum: 3, pageSize: defaultPageSize };
        let qs = parse(buildSearchParams(p));
        expect(qs.get('pageNum')).toBe('3');

        p.pageNum = 1;
        qs = parse(buildSearchParams(p));
        expect(qs.has('pageNum')).toBe(false);
    });

    it('should combines multiple params in one string', () => {
        const p = {
            mode: 'list',
            genres: ['RPG'],
            types: ['Action'],
            sortBy: constants.sortsOptions[1].value,
            minPrice: 10,
            maxPrice: 5000,
            pageNum: 2,
            pageSize: constants.pageSizesOptions[1].value,
        };
        const qs = parse(buildSearchParams(p));
        expect(qs.get('genre')).toBe('RPG');
        expect(qs.get('type')).toBe('Action');
        expect(qs.get('sortBy')).toBe(p.sortBy);
        expect(qs.get('minPrice')).toBe('10');
        expect(qs.get('maxPrice')).toBe('5000');
        expect(qs.get('pageNum')).toBe('2');
        expect(qs.get('pageSize')).toBe(String(p.pageSize));
    });
});
