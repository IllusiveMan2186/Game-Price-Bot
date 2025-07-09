import React, { useState, useCallback, useMemo } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  setGenres, setTypes, setMinPrice, setMaxPrice, setPageNum
} from '@features/params/paramsSlice';

import { useNavigation } from '@contexts/NavigationContext';
import { reloadPage } from '@util/navigationUtils';
import * as constants from '@util/constants';
import Message from '@util/message';

import PriceFilterSection from '@components/game/list/filter/price/PriceFilterSection';
import CheckboxGroupSection from '@components/game/list/filter/checkbox/CheckboxGroupSection';

import './GameListFilter.css';

export default function GameListFilter() {
  const dispatch = useDispatch();
  const { genres, types, minPrice, maxPrice } = useSelector((state) => state.params);
  const navigate = useNavigation();

  const [formState, setFormState] = useState({ minPrice, maxPrice });
  const [isFormChanged, setFormChanged] = useState(false);
  const [priceError, setPriceError] = useState('');

  const handlePriceChange = useCallback((event) => {
    const { name, value } = event.target;
    const sanitizedValue = Math.max(0, +value);
    const updatedState = { ...formState, [name]: sanitizedValue };

    setFormState(updatedState);
    setFormChanged(true);

    setPriceError(updatedState.minPrice <= updatedState.maxPrice ? '' : <Message string="app.game.error.price" />);
  }, [formState]);

  const handleFilterButtonClick = useCallback(() => {
    dispatch(setMinPrice(formState.minPrice));
    dispatch(setMaxPrice(formState.maxPrice));
    dispatch(setPageNum(1));
    reloadPage(navigate);
  }, [dispatch, formState, navigate]);

  const handleCheckboxChange = useCallback((event, isNotExcludedFieldType) => {
    const { name, value, checked } = event.target;
    const upperValue = value.toUpperCase();
    const currentValues = name === 'genre' ? genres : name === 'type' ? types : [];

    const updatedValues = checked === isNotExcludedFieldType
      ? [...new Set([...currentValues, upperValue])]
      : currentValues.filter((v) => v !== upperValue);

    if (name === 'genre') dispatch(setGenres(updatedValues));
    else if (name === 'type') dispatch(setTypes(updatedValues));

    dispatch(setPageNum(1));
    setFormChanged(true);
  }, [dispatch, genres, types]);

  const isChecked = useCallback((value, fieldValue, isNotExcludedFieldType) => {
    const upperValue = value.toUpperCase();
    return isNotExcludedFieldType === fieldValue.includes(upperValue);
  }, [genres, types]);

  const isFormValid = useMemo(() => priceError === '' && isFormChanged, [priceError, isFormChanged]);

  return (
    <aside className="col-lg-3 App-game-filter">
      <div className="App-game-filter-title">
        <Message string="app.game.filter.title" />
      </div>

      <div className="App-game-filter-subdiv">
        <PriceFilterSection
          minPrice={formState.minPrice}
          maxPrice={formState.maxPrice}
          onChange={handlePriceChange}
          error={priceError}
        />

        <CheckboxGroupSection
          title={<Message string="app.game.filter.genre.title" />}
          options={constants.ganresOptions}
          fieldName="genre"
          fieldValue={genres}
          isNotExcludedFieldType={true}
          onChange={handleCheckboxChange}
          isChecked={isChecked}
        />

        <CheckboxGroupSection
          title={<Message string="app.game.info.type" />}
          options={constants.productTypesOptions}
          fieldName="type"
          fieldValue={types}
          isNotExcludedFieldType={false}
          onChange={handleCheckboxChange}
          isChecked={isChecked}
        />

        <button
          type="submit"
          className="btn btn-primary btn-block mb-3"
          disabled={!isFormValid}
          onClick={handleFilterButtonClick}
        >
          <Message string="app.game.filter.accept.button" />
        </button>
      </div>
    </aside>
  );
}
