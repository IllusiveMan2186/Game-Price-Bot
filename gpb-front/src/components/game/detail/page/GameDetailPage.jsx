import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

import { getGameRequest } from '../../../../services/gameRequests';
import GameImage from '../../shared/GameImage';
import GameDetails from '../details/GameDetails';

import './GameDetailPage.css';

const GameDetailPage = () => {
  const [game, setGame] = useState(null);
  const navigate = useNavigate();
  const { gameId } = useParams();

  useEffect(() => {
    const fetchGame = async () => {
      try {
        await getGameRequest(gameId, setGame, navigate);
      } catch (error) {
        console.error('Failed to fetch game details:', error);
        navigate('/error'); // Redirect to error page
      }
    };
    fetchGame();
  }, [gameId, navigate]);

  if (!game) return <div>Loading...</div>;

  return (
    <div className="App-game">
      <div className="App-game-page-template">
        <div className="App-game-page">
          <div className="App-game-page-image">
            <GameImage className="App-game-content-list-game-info-img" gameName={game.name} />
          </div>
          <div className="App-game-page-info-half">
            <GameDetails game={game} gameId={gameId} navigate={navigate} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default GameDetailPage;
