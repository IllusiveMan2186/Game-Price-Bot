import reducer, {
  setSearch,
  setPageNum,
  setSortBy,
  resetParams,
} from '@features/params/paramsSlice';
import * as constants from '@util/constants';

describe('paramsSlice', () => {
  const initial = {
    games: [],
    mode: '',
    search: '',
    genres: [],
    types: [],
    sortBy: constants.sortsOptions[0].value,
    minPrice: 0,
    maxPrice: 10000,
    elementAmount: 0,
    pageNum: 1,
    pageSize: constants.pageSizesOptions[0].value,
  };

  it('should return the initial state', () => {
    expect(reducer(undefined, { type: '@@INIT' })).toEqual(initial);
  });

  it('should handle setSearch', () => {
    const next = reducer(initial, setSearch('hello'));
    expect(next.search).toBe('hello');
  });

  it('should handle setPageNum', () => {
    const next = reducer(initial, setPageNum(5));
    expect(next.pageNum).toBe(5);
  });

  it('should handle setSortBy', () => {
    const next = reducer(initial, setSortBy('price-ASC'));
    expect(next.sortBy).toBe('price-ASC');
  });

  it('should reset to initial state', () => {
    const modified = { ...initial, search: 'foo', pageNum: 3, sortBy: 'price-ASC' };
    expect(reducer(modified, resetParams())).toEqual(initial);
  });
});
