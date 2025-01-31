import React, { useCallback } from 'react';

import { isUserAdmin } from '@util/userDataUtils';
import { useIsUserAuth } from '@util/authHook';
import Message from '@util/message';

import { CommonGameInfo } from '@components/game/shared/info/CommonGameInfo';
import GameStoresList from '@components/game/detail/stores/GameStoresList';

import {
    subscribeForGameRequest,
    unsubscribeForGameRequest,
    removeGameRequest,
} from '@services/gameRequests';

import './GameDetails.css';

const RemoveButton = ({ gameId, navigate }) => (
    <button
        type="button"
        className="btn btn-danger btn-block mb-3 App-game-page-info-remove"
        onClick={() => removeGameRequest(gameId, navigate)}
    >
        <Message string="app.game.info.remove" />
    </button>
);

const SubscribeButton = ({ isSubscribed, gameId, navigate }) => {
    const handleSubscribe = useCallback(() => {
        if (isSubscribed) {
            unsubscribeForGameRequest(gameId, navigate);
        } else {
            subscribeForGameRequest(gameId, navigate);
        }
    }, [isSubscribed, gameId, navigate]);

    const isUserAuthenticate = useIsUserAuth();

    return (
        <div className="App-game-page-info-subscribe">
            <button
                id="subscribe-button"
                type="button"
                className="btn btn-primary btn-block mb-3"
                disabled={!isUserAuthenticate}
                onClick={handleSubscribe}
            >
                <Message string={isSubscribed ? 'app.game.info.unsubscribe' : 'app.game.info.subscribe'} />
            </button>
            {!isUserAuthenticate && <Message string="app.game.info.need.auth" />}
        </div>
    );
};

const GameGenres = ({ genres }) => (
    <div className="App-game-page-info-common-genre">
        <Message string="app.game.filter.genre.title" />:
        {genres.map((genre) => (
            <span key={genre} className="genre-subtext">
                <Message string={`app.game.genre.${genre.toLowerCase()}`} />
            </span>
        ))}
    </div>
);

const GameDetails = ({ game, gameId, navigate }) => (
    <div className="App-game-page-info">
        <h1 className="App-game-page-info-title">{game.name}</h1>
        <div className="App-game-page-info-common">
            <CommonGameInfo game={game} className="App-game-page-info-common-price" />
            <GameGenres genres={game.genres} />
        </div>
        <SubscribeButton isSubscribed={game.userSubscribed} gameId={gameId} navigate={navigate} />
        {isUserAdmin() && <RemoveButton gameId={gameId} navigate={navigate} />}
        <GameStoresList stores={game.gamesInShop} />
    </div>
);

export default GameDetails;
