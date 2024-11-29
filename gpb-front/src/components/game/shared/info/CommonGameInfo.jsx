import React from 'react';

import { ProductType } from '@components/game/shared/type/ProductType';
import { GameAvailability } from '@components/game/shared/availability/GameAvailability';

export const CommonGameInfo = ({ game, className }) => (
  <div className={className}>
    <ProductType type={game.type} />
    <GameAvailability available={game.available} />
    <div className="App-game-content-list-game-info-price">
      {game.minPrice} - {game.maxPrice} ₴
    </div>
  </div>
);
