import * as React from 'react';
import { useNavigate } from 'react-router-dom';

import GameImage from '../../shared/GameImage';
import { CommonGameInfo } from '../../shared/info/CommonGameInfo';

import './GameList.css';

const GameList = ({ games = [] }) => { // Destructure `games` from the passed object
    const navigate = useNavigate();

    if (!Array.isArray(games)) { // Safeguard to ensure `games` is an array
        console.error("Expected games to be an array but got:", games);
        return <div>No games available</div>;
    }

    return (
        <div className="App-game-content-list">
            {games.map(game => (
                <div
                    key={game.id} // Ensure each child element has a unique key
                    className="App-game-content-list-game"
                    onClick={() => navigate('/game/' + game.id)}
                >
                    <GameImage
                        className="App-game-content-list-game-info-img"
                        gameName={game.name}
                    />
                    <div className="App-game-content-list-game-info">
                        <div className="App-game-content-list-game-info-title">
                            {game.name}
                        </div>
                        <CommonGameInfo
                            game={game}
                            className="App-game-content-list-game-info-bottom"
                        />
                    </div>
                </div>
            ))}
        </div>
    );
};

export default GameList;
