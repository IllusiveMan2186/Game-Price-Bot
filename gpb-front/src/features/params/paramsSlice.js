import { createSlice } from '@reduxjs/toolkit';

import * as constants from '@util/constants';

const initialState = {
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

const paramsSlice = createSlice({
  name: 'params',
  initialState,
  reducers: {
    setGames: (state, action) => {
      state.games = action.payload;
    },
    setMode: (state, action) => {
      state.mode = action.payload;
    },
    setSearch: (state, action) => {
      state.search = action.payload;
    },
    setGenres: (state, action) => {
      state.genres = action.payload;
    },
    setTypes: (state, action) => {
      state.types = action.payload;
    },
    setSortBy: (state, action) => {
      state.sortBy = action.payload;
    },
    setMinPrice: (state, action) => {
      state.minPrice = action.payload;
    },
    setMaxPrice: (state, action) => {
      state.maxPrice = action.payload;
    },
    setElementAmount: (state, action) => {
      state.elementAmount = action.payload;
    },
    setPageNum: (state, action) => {
      state.pageNum = action.payload;
    },
    setPageSize: (state, action) => {
      state.pageSize = action.payload;
    },
    resetParams: () => initialState,
  },
});

export const {
  setGames,
  setMode,
  setSearch,
  setGenres,
  setTypes,
  setSortBy,
  setSortDirection,
  setMinPrice,
  setMaxPrice,
  setElementAmount,
  setPageNum,
  setPageSize,
  resetParams,
} = paramsSlice.actions;

export default paramsSlice.reducer;
