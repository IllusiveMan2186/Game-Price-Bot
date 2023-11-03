import * as React from 'react'
import Message from './Message';
import {GameImage,GameAvailability} from './GameImage';

export default class GameContent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            getGameI: props.getGameI,
            games: props.games
        }
    };

    getGameInformation = (gameId) => {
        this.state.getGameI(gameId)
    };


    render() {
        return (

            this.state.games.map(game => {
                return (
                    <div class="App-game-content-list-game " onClick={() => this.getGameInformation(game.id)}>
                        <GameImage className="App-game-content-list-game-info-img" gameName={game.name} />
                        <div class="App-game-content-list-game-info">
                            <div class="App-game-content-list-game-info-title">
                                {game.name}
                            </div>
                            <div class="App-game-content-list-game-info-genre">

                            </div>
                            <div class="App-game-content-list-game-info-bottom">
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
    };
}