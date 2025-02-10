import { useState, useEffect, useCallback } from 'react';

import { isUserAdmin } from '@util/userDataUtils';
import { useIsUserAuth } from '@util/authHook';
import Message from '@util/message';

import { CommonGameInfo } from '@components/game/shared/info/CommonGameInfo';
import GameStoresList from '@components/game/detail/details/stores/list/GameStoresList';
import AddGameInStore from '@components/game/detail/details/stores/adding/AddGameInStore';

import {
    subscribeForGameRequest,
    unsubscribeForGameRequest,
    removeGameRequest,
} from '@services/gameRequests';

import './GameDetails.css';

const RemoveButton = ({ gameId, navigate }) => (
    <button
        type="button"
        className="btn btn-danger btn-block mb-3 app-game__remove"
        onClick={() => removeGameRequest(gameId, navigate)}
    >
        <Message string="app.game.info.remove" />
    </button>
);

const SubscribeButton = ({ isSubscribed: initialSubscribed, gameId }) => {
    const [isSubscribed, setIsSubscribed] = useState(initialSubscribed);

    useEffect(() => {
        setIsSubscribed(initialSubscribed);
    }, [initialSubscribed]);

    const handleSubscribe = useCallback(() => {
        if (isSubscribed) {
            unsubscribeForGameRequest(gameId);
            setIsSubscribed(false);
        } else {
            subscribeForGameRequest(gameId);
            setIsSubscribed(true);
        }
    }, [isSubscribed, gameId]);

    const isUserAuthenticate = useIsUserAuth();

    return (
        <div className="app-game__subscribe">
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
    <div className="app-game__details-genre">
        <Message string="app.game.filter.genre.title" />:
        {genres.map((genre) => (
            <span key={genre} className="app-game__genre-subtext">
                <Message string={`app.game.genre.${genre.toLowerCase()}`} />
            </span>
        ))}
    </div>
);

const GameDetails = ({ game, gameId, navigate }) => (
    <div className="app-game__info">
        <h1 className="app-game__title">{game.name}</h1>
        <div className="app-game__details">
            <CommonGameInfo game={game} className="app-game__details-price" />
            <GameGenres genres={game.genres} />
        </div>
        <SubscribeButton isSubscribed={game.userSubscribed} gameId={gameId} />
        {isUserAdmin() && <RemoveButton gameId={gameId} navigate={navigate} />}
        <GameStoresList stores={game.gamesInShop} navigate={navigate} />
        {isUserAdmin() && <AddGameInStore gameId={gameId} />}
    </div>
);

export default GameDetails;
