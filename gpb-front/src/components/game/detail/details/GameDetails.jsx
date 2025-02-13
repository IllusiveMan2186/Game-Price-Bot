import { useState, useEffect, useCallback } from 'react';

import { useAuth } from "@contexts/AuthContext";
import Message from '@util/message';

import { CommonGameInfo } from '@components/game/shared/info/CommonGameInfo';
import GameStoresList from '@components/game/detail/details/stores/list/GameStoresList';
import AddGameInStore from '@components/game/detail/details/stores/adding/AddGameInStore';

import { useGameActions, } from '@hooks/game/useGameActions';
import { useGameSubscription, } from '@hooks/game/useGameSubscription';

import './GameDetails.css';

const RemoveButton = ({ gameId }) => {
    const { removeGameRequest } = useGameActions();

    return (
        <button
            type="button"
            className="btn btn-danger btn-block mb-3 app-game__remove"
            onClick={() => removeGameRequest(gameId)}
        >
            <Message string="app.game.info.remove" />
        </button>
    );
};

const SubscribeButton = ({ isSubscribed: initialSubscribed, gameId }) => {
    const [isSubscribed, setIsSubscribed] = useState(initialSubscribed);
    const { isUserAuth } = useAuth();
    const { subscribeForGameRequest, unsubscribeForGameRequest } = useGameSubscription();

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

    return (
        <div className="app-game__subscribe">
            <button
                id="subscribe-button"
                type="button"
                className="btn btn-primary btn-block mb-3"
                disabled={!isUserAuth}
                onClick={handleSubscribe}
            >
                <Message string={isSubscribed ? 'app.game.info.unsubscribe' : 'app.game.info.subscribe'} />
            </button>
            {!isUserAuth && <Message string="app.game.info.need.auth" />}
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

const GameDetails = ({ game, gameId, navigate }) => {
    const { isUserAdmin } = useAuth();

    return (
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
};


export default GameDetails;
