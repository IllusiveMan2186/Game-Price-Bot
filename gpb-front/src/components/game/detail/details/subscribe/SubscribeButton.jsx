import { useState, useEffect, useCallback } from 'react';

import { useAuth } from "@contexts/AuthContext";
import Message from '@util/message';

import { useGameSubscription, } from '@hooks/game/useGameSubscription';

import './SubscribeButton.css';


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
                disabled={!isUserAuth()}
                onClick={handleSubscribe}
            >
                <Message string={isSubscribed ? 'app.game.info.unsubscribe' : 'app.game.info.subscribe'} />
            </button>
            {!isUserAuth() && <Message string="app.game.info.need.auth" />}
        </div>
    );
};

export default SubscribeButton;