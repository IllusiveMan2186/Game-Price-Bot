import * as React from 'react'
import Message from './Message';
import { GameImage, GameAvailability } from './GameImage';
import { useParams } from 'react-router-dom'
import { request } from '../helpers/axios_helper';
import { isUserAuth } from '../helpers/axios_helper';
import { useNavigate } from 'react-router-dom'

export default function GameInfo(props) {

    const [game, setGame] = React.useState(null);

    let { gameId } = useParams();
    React.useEffect(() => {
        request('GET', '/game/' + gameId).then((response) => {
            setGame(response.data);
        });
    }, []);

    const navigate = useNavigate();

    const subscribe = () => {
        request('POST', '/user/games/' + gameId, {}).then((response) => {
            navigate(0)
        });
    }

    const unsubscribe = () => {
        request('DELETE', '/user/games/' + gameId, {}).then((response) => {
            navigate(0)
        });
    }

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
                            <SubscribeButton isSubscribed={game.userSubscribed} subscribe={subscribe}
                                unsubscribe={unsubscribe} />
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
        listItems.push(<div class="App-game-page-info-common-genre"><Message string={'app.game.genre.' + genre.toLowerCase()} /></div>)
    })
    return listItems

}

function GameInStoreList(props) {
    const listItems = [];
    props.stores.map(gameInStore => {
        let domain = (new URL(gameInStore.url)).hostname;
        let image = require(`../img/${domain}.png`)
        listItems.push(<a class="App-game-page-info-storeList-store " href={gameInStore.url} target="_blank">
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

function SubscribeButton(props) {

    return (
        <div class="App-game-page-info-subscribe">
            <button type="submit" className="btn btn-primary btn-block mb-3"
                disabled={!isUserAuth()} onClick={() => props.isSubscribed ? props.unsubscribe() : props.subscribe()}>
                {props.isSubscribed ? <Message string={'app.game.info.unsubscribe'} /> : <Message string={'app.game.info.subscribe'} />}
            </button>
            <span>{!isUserAuth() && <Message string={'app.game.info.need.auth'} />}</span>
        </div>
    )
}