import * as React from 'react'
import { GameImage, GameAvailability, ProductType } from './GameHelper';
import { useNavigate } from "react-router-dom";


export default function GameContent(props) {
    const navigate = useNavigate();

    let games = props.games;


    return (
        games.map(game => {
            return (
                <div class="App-game-content-list-game " onClick={() => navigate('/game/' + game.id)}>
                    <GameImage className="App-game-content-list-game-info-img" gameName={game.name} />
                    <div class="App-game-content-list-game-info">
                        <div class="App-game-content-list-game-info-title">
                            {game.name}
                        </div>
                        <div class="App-game-content-list-game-info-bottom">
                            <ProductType type={game.type} />
                            <GameAvailability available={game.available} />
                            <div class="App-game-content-list-game-info-price">
                                {game.minPrice} - {game.maxPrice} ₴
                            </div>
                        </div>
                    </div>
                </div >
            );
        })


    );
}