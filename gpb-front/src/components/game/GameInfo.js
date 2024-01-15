import * as React from 'react'

import '../../styles/gameInfo.css';

import Message from '../../util/message';
import { GameImage, GameAvailability, ProductType } from './GameHelper';
import { useParams } from 'react-router-dom'
import { isUserAdmin, isUserAuth } from '../../util/axios_helper';
import { getGameRequest, subscribeForGameRequest, unsubscribeForGameRequest, removeGameRequest } from '../../request/gameRequests';
import { useNavigate } from 'react-router-dom';

export default function GameInfo(props) {

    const [game, setGame] = React.useState(null);
    const navigate = useNavigate();

    let { gameId } = useParams();
    React.useEffect(() => {
        getGameRequest(gameId, setGame, navigate)
    }, []);

    if (!game) return null;

    return (
        <div class='App-game'>
            <div class="App-game-page-template">
                <div class="App-game-page">
                    <div class="App-game-page-image">
                        <GameImage className="App-game-content-list-game-info-img" gameName={game.name} />
                    </div>
                    <div class="App-game-page-info-half">
                        <div class="App-game-page-info">
                            <div class="App-game-page-info-title">
                                {game.name}
                            </div>
                            <div class="App-game-page-info-common  ">
                                <div class="App-game-page-info-common-price">
                                    <ProductType type={game.type} />
                                    <GameAvailability available={game.available} />
                                    <div class="App-game-content-list-game-info-price">
                                        {game.minPrice} - {game.maxPrice} ₴
                                    </div>
                                </div>
                                <div class="App-game-page-info-common-genre">
                                    <Message string={'app.game.filter.genre.title'} />:
                                    <GenreList genres={game.genres} />
                                </div>
                            </div>
                            <SubscribeButton isSubscribed={game.userSubscribed} gameId={gameId} navigate={navigate} />
                            {isUserAdmin() && <RemoveButton gameId={gameId} navigate={navigate} />}
                            <div class="App-game-page-info-storeList">
                                <GameInStoreList stores={game.gamesInShop} />
                            </div>
                        </div>
                    </div>
                </div >
            </div >
        </div >
    );
};

function GenreList(props) {
    const listItems = [];
    props.genres.map(genre => {
        listItems.push(<div className="App-game-page-info-common-genre genre-subtext"><Message string={'app.game.genre.' + genre.toLowerCase()} /></div>)
    })
    return listItems
}

function GameInStoreList(props) {
    const listItems = [];
    props.stores.map(gameInStore => {
        let domain = (new URL(gameInStore.url)).hostname;
        let image = require(`../../img/${domain}.png`)
        console.info(gameInStore.clientType)
        listItems.push(<a class="App-game-page-info-storeList-store " href={gameInStore.url} target="_blank">
            <img src={image} />
            <ClientActivationType clientType={gameInStore.clientType} />
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

function SubscribeButton(props) {
    return (
        <div class="App-game-page-info-subscribe">
            <button type="submit" className="btn btn-primary btn-block mb-3" disabled={!isUserAuth()}
                onClick={() => props.isSubscribed ? unsubscribeForGameRequest(props.gameId, props.navigate) : subscribeForGameRequest(props.gameId, props.navigate)}>
                {props.isSubscribed ? <Message string={'app.game.info.unsubscribe'} /> : <Message string={'app.game.info.subscribe'} />}
            </button>
            <span>{!isUserAuth() && <Message string={'app.game.info.need.auth'} />}</span>
        </div>
    )
}

function ClientActivationType(props) {
    if (props.clientType) {
        let image = require(`../../img/${props.clientType.toLowerCase()}.png`)
        return (
            <img src={image} />
        )
    }
}

function RemoveButton(props) {
    return (
        <div class="App-game-page-info-subscribe">
            <button type="submit" className="btn btn-primary btn-block mb-3 App-game-page-info-remove"
                onClick={removeGameRequest(props.gameId, props.navigate)}>
                <Message string={'app.game.info.remove'} />
            </button>
        </div>
    )
}