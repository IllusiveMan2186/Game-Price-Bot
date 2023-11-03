import * as React from 'react'
import Message from './Message';
import { GameImage, GameAvailability } from './GameImage';

export default function GameInfo(props) {
    return (
        <div class='App-game'>
            <div class="App-game-page-template">
                <div class="App-game-page">
                    <div class="App-game-page-image">
                        <GameImage className="App-game-content-list-game-info-img" gameName={props.game.name} />
                    </div>
                    <div class="App-game-page-info-half">
                        <div class="App-game-page-info">


                            <div class="App-game-page-info-title">
                                {props.game.name}
                            </div>
                            <div class="App-game-page-info-common  ">
                                <div class="App-game-page-info-common-price">

                                    <GameAvailability available={props.game.available} />
                                    <div class="App-game-content-list-game-info-price">
                                        {props.game.minPrice} - {props.game.maxPrice} ₴
                                    </div>
                                </div>
                                <div class="App-game-page-info-common-genre">
                                    <Message string={'app.game.filter.genre.title'} />:
                                    <GenreList genres={props.game.genres} />
                                </div>
                            </div>
                            <div class="App-game-page-info-storeList">
                                <GameInStoreList stores={props.game.gamesInShop} />
                            </div>
                        </div>
                    </div>
                </div >
            </div >
        </div >


    );


}

function GenreList(props) {
    const listItems = [];
    props.genres.map(genre => {
        listItems.push(<div class="App-game-page-info-common-genre"><Message string={'app.game.genre.' + genre.toLowerCase()} /></div>)
    })
    return listItems

}


function GameInStoreList(props) {
    const listItems = [];
    props.stores.map(gameInStore => {
        let domain = (new URL(gameInStore.url)).hostname;
        let image = require(`../img/${domain}.png`)
        listItems.push(<a class="App-game-page-info-storeList-store " href={gameInStore.url}>
            <img src={image} />
            <div class="">{domain}</div>
            <GameAvailability available={gameInStore.available} />
            <div class="App-game-page-info-storeList-store-price-section">
                <div class="App-game-page-info-storeList-store-price">{gameInStore.price}</div>
                <div class="App-game-page-info-storeList-store-discount">-{gameInStore.discount}%</div>
                <div class="App-game-page-info-storeList-store-discountPrice">{gameInStore.discountPrice}</div>
            </div>
        </a>)
    })
    return listItems

}