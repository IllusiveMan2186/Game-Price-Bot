import React from 'react';

import Message from '@util/message';

import './GameAvailability.css'

export function GameAvailability({ available }) {
  return (
    <div className={available ? "app-game-available" : "app-game-available not-available"}>
      {available ? <Message string="app.game.is.available" /> : <Message string="app.game.not.available" />}
    </div>
  );
}
