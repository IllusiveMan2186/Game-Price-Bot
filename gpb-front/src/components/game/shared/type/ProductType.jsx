import React from 'react';

import Message from '@util/message';

import './ProductType.css';

export function ProductType({ type }) {
  if (type) {
    return (
      <div className="app-game-type">
        <Message string={`app.game.info.type.${type.toLowerCase()}`} />
      </div>
    );
  }
  return null;
}
