import * as React from 'react'
import pic from '../img/d12.jpg';
import Message from './Message';


export default function GameContent(props) {
    return (

        props.games.map(game => {
            return (
                <div class="App-game-content-list-game ">

                    <img class="App-game-content-list-game-info-img" src={pic}></img>
                    <div class="App-game-content-list-game-info">
                        <div class="App-game-content-list-game-info-title">
                            {game.name}
                        </div>
                        <div class="App-game-content-list-game-info-genre">

                        </div>
                        <div class="App-game-content-list-game-info-bottom">
                            <div class="App-game-content-list-game-info-available">
                                {game.available ? <Message string={'app.game.is.available'} /> 
                                : <Message string={'app.game.not.available'} />}
                            </div>
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