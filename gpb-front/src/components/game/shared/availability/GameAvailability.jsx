import React from 'react';

import Message from '../../../../util/message';

import './GameAvailability.css'

export function GameAvailability({ available }) {
  return (
    <div className={available ? "App-game-content-list-game-info-available" : "App-game-content-list-game-info-available not-available"}>
      {available ? <Message string="app.game.is.available" /> : <Message string="app.game.not.available" />}
    </div>
  );
}
