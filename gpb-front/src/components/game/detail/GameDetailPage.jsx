import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useNavigation } from "@contexts/NavigationContext";
import { useGameActions } from '@hooks/game/useGameActions';
import Message from '@util/message';
import GameImage from '@components/game/shared/GameImage';
import GameDetails from '@components/game/detail/details/GameDetails';
import Loading from '@components/game/shared/loading/Loading';

import './GameDetailPage.css';

const GameDetailPage = () => {
  const [game, setGame] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigation();
  const { gameId } = useParams();
  const { getGameRequest } = useGameActions();

  useEffect(() => {
    const fetchGame = async () => {
      await getGameRequest(gameId, setGame, setError);
    };
    fetchGame();
  }, [gameId, navigate]);

  if (error) return <Message string={error} />;
  if (!game) return <Loading />;

  return (
    <div className="app-game">
      <div className="app-game-page-template">
        <div className="app-game-page">
          <div className="app-game-page-image">
            <GameImage gameName={game.name} />
          </div>
          <div className="app-game-page-info">
            <GameDetails game={game} gameId={gameId} navigate={navigate} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default GameDetailPage;
