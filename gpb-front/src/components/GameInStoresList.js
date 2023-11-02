import * as React from 'react'
import Message from './Message';
import GameImage from './GameImage';
import { request } from '../helpers/axios_helper';

export default function GameInStoreList(props) {
    return (
        <div class='App-game'>
            <div class="App-game-page-template">
                <div class="App-game-page">
                    <div class="App-game-page-image">
                        <GameImage className="App-game-content-list-game-info-img" gameName={props.game.name} />
                    </div>
                    <div class="App-game-page-info">


                        <div class="App-game-page-info-title">
                            {props.game.name}
                        </div>
                        <div class="App-game-page-info-common  ">
                            <div class="App-game-page-info-common-price">
                                <div class="App-game-content-list-game-info-available">
                                    {props.game.available ? <Message string={'app.game.is.available'} />
                                        : <Message string={'app.game.not.available'} />}
                                </div>
                                <div class="App-game-content-list-game-info-price">
                                    {props.game.minPrice} - {props.game.maxPrice} ₴
                                </div>
                            </div>
                            <div class="App-game-page-info-common-genre">
                                <GenreList genres={props.game.genres} />
                            </div>
                        </div>
                        <div class="App-game-page-info-gameInShope">
                            {props.game.name}
                        </div>
                    </div>
                </div >
            </div >
        </div >


    );


}