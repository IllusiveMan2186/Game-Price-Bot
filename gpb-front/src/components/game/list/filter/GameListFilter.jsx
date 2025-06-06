import React, { useState, useCallback, useMemo } from 'react';

import * as constants from '@util/constants';
import Message from '@util/message';

import './GameListFilter.css';

export default function GameListFilter({ searchParams, parameterSetOrRemove, reloadPage, setPage }) {

  const getParameterOrDefaultValue = (parameter, defaultValue) => {
    return parameter !== null ? parameter : defaultValue;
  }

  const [formState, setFormState] = useState({
    minPrice: +getParameterOrDefaultValue(searchParams.get("minPrice"), 0),
    maxPrice: +getParameterOrDefaultValue(searchParams.get("maxPrice"), 10000),
  });
  const [isFormChanged, setFormChanged] = useState(false);
  const [priceError, setPriceError] = useState("");

  const handleFilterButtonClick = useCallback(() => {
    parameterSetOrRemove("minPrice", formState.minPrice, 0);
    parameterSetOrRemove("maxPrice", formState.maxPrice, 10000);
    reloadPage();
  }, [formState, parameterSetOrRemove, reloadPage]);

  const handlePriceChange = useCallback(
    (event) => {
      const { name, value } = event.target;
      
      let sanitizedValue = Math.max(0, +value); 
  
      setFormState((prev) => ({ ...prev, [name]: sanitizedValue }));
      setFormChanged(true);
  
      const { minPrice, maxPrice } = { ...formState, [name]: sanitizedValue };
      if (minPrice <= maxPrice) {
        setPriceError("");
      } else {
        setPriceError(<Message string="app.game.error.price" />);
      }
    },
    [formState]
  );  

  const isChecked = useCallback(
    (value, field, isNotExcludedFieldType) =>
      searchParams.has(field, value.toUpperCase()) === isNotExcludedFieldType,
    [searchParams]
  );

  const handleCheckboxChange = useCallback(
    (event, isNotExcludedFieldType) => {
      const { name, value, checked } = event.target;

      // Handle adding or removing the search parameter
      const shouldAdd = checked === isNotExcludedFieldType;
      const normalizedValue = value.toUpperCase();
  
      if (shouldAdd) {
        searchParams.append(name, normalizedValue);
      } else {
        searchParams.delete(name, normalizedValue);
      }
  
      // Reset pagination and mark form as changed
      setPage(1);
      setFormChanged(true);
    },
    [searchParams, setPage]
  );

  const isFormValid = useMemo(() => {
    return priceError === "" && isFormChanged;
  }, [priceError, isFormChanged]);

  const renderCheckboxGroup = (options, fieldName, isNotExcludedFieldType) =>
    options.map((option) => (
      <label key={option.value} className="App-game-filter-genre-button">
        <input
          type="checkbox"
          className="App-game-filter-genre-button-checkbox"
          name={fieldName}
          value={option.value}
          onChange={(event) => handleCheckboxChange(event, isNotExcludedFieldType)}
          defaultChecked={isChecked(option.value, fieldName, isNotExcludedFieldType)}
        />
        <span className="App-game-filter-genre-button-text">{option.label}</span>
      </label>
    ));

  return (
    <aside className="col-lg-3 App-game-filter">
      <div className="App-game-filter-title">
        <Message string="app.game.filter.title" />
      </div>
      <div className="App-game-filter-subdiv">
        {/* Price Section */}
        <div className="App-game-filter-section">
          <div className="App-game-filter-title">
            <Message string="app.game.filter.price.title" />
          </div>
          <div className="App-game-filter-price-inputs">
            <input
              type="number"
              min="0"
              name="minPrice"
              value={formState.minPrice}
              onChange={handlePriceChange}
            />
            <span>-</span>
            <input
              type="number"
              min="0"
              name="maxPrice"
              value={formState.maxPrice}
              onChange={handlePriceChange}
            />
            <span>₴</span>
          </div>
          {priceError && <span className="Error">{priceError}</span>}
        </div>

        {/* Genre Section */}
        <div className="App-game-filter-section">
          <div className="App-game-filter-title">
            <Message string="app.game.filter.genre.title" />
          </div>
          <div className="App-game-filter-genre">
            {renderCheckboxGroup(constants.ganresOptions, "genre", true)}
          </div>
        </div>

        {/* Product Type Section */}
        <div className="App-game-filter-section">
          <div className="App-game-filter-title">
            <Message string="app.game.info.type" />
          </div>
          <div className="App-game-filter-genre">
            {renderCheckboxGroup(constants.productTypesOptions, "type", false)}
          </div>
        </div>

        {/* Submit Button */}
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
