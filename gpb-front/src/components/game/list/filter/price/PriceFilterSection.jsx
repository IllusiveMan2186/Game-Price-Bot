import Message from '@util/message';

import './PriceFilterSection.css';

export default function PriceFilterSection({ minPrice, maxPrice, onChange, error }) {
  return (
    <div className="App-game-filter-section">
      <div className="App-game-filter-title">
        <Message string="app.game.filter.price.title" />
      </div>
      <div className="App-game-filter-price-inputs">
        <input
          type="number"
          min="0"
          name="minPrice"
          value={minPrice}
          onChange={onChange}
        />
        <span>-</span>
        <input
          type="number"
          min="0"
          name="maxPrice"
          value={maxPrice}
          onChange={onChange}
        />
        <span>₴</span>
      </div>
      {error && <span className="Error">{error}</span>}
    </div>
  );
}
