import { configureStore } from '@reduxjs/toolkit';
import paramsReducer from '@features/params/paramsSlice';

export const paramsStore = configureStore({
  reducer: {
    params: paramsReducer,
  },
});
