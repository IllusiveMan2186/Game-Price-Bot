import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useNavigation } from "@contexts/NavigationContext";

import { useGameActions } from '@hooks/game/useGameActions';
import GameImage from '@components/game/shared/GameImage';
import GameDetails from '@components/game/detail/details/GameDetails';
import Loading from '@components/game/shared/loading/Loading';

import './GameDetailPage.css';

const GameDetailPage = () => {
  const [game, setGame] = useState(null);
  const navigate = useNavigation();
  const { gameId } = useParams();
  const { getGameRequest } = useGameActions();

  useEffect(() => {
    const fetchGame = async () => {
      try {
        await getGameRequest(gameId, setGame);
      } catch (error) {
        console.error('Failed to fetch game details:', error);
        navigate('/error'); // Redirect to error page
      }
    };
    fetchGame();
  }, [gameId, navigate]);

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
