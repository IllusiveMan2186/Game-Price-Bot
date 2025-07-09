import * as React from 'react';
import { useNavigation } from "@contexts/NavigationContext";
import { useSelector } from 'react-redux';
import GameImage from '@components/game/shared/image/GameImage';
import { CommonGameInfo } from '@components/game/shared/info/CommonGameInfo';

import './GameList.css';

const GameList = ({ }) => { // Destructure `games` from the passed object
    const navigate = useNavigation();
    const { games } = useSelector((state) => state.params);

    if (!Array.isArray(games)) { // Safeguard to ensure `games` is an array
        console.error("Expected games to be an array but got:", games);
        return <div>No games available</div>;
    }

    return (
        <div className="app-list">
            {games.map(game => (
                <div
                    key={game.id} // Ensure each child element has a unique key
                    className="app-list__game"
                    onClick={() => navigate('/game/' + game.id)}
                >
                    <GameImage
                        className="app-list__game-img"
                        gameName={game.name}
                    />
                    <div className="app-list__game-info">
                        <div className="app-list__game-title ">
                            {game.name}
                        </div>
                        <CommonGameInfo
                            game={game}
                            className="app-list__game-bottom"
                        />
                    </div>
                </div>
            ))}
        </div>
    );
};

export default GameList;
