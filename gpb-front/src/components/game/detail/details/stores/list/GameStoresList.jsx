import React, { useState, useMemo } from 'react';
import { GameAvailability } from '@components/game/shared/availability/GameAvailability';
import { useAuth } from "@contexts/AuthContext";
import { useGameStoreActions, } from '@hooks/game/useGameStoreActions';
import trash from '@assets/images/trash.png';
import './GameStoresList.css';

const ClientActivationType = ({ clientType }) => {
    if (!clientType) return null;
    const imagePath = `/assets/images/${clientType.toLowerCase()}.png`;

    return <img src={imagePath} alt={clientType} onError={(e) => (e.target.style.display = 'none')} />;
};

const StorePriceInfo = ({ price, discount, discountPrice }) => (
    <div className="app-store__price-section">
        {discount > 0 && discountPrice < price && (
            <>
                <div className="app-store__price">{price}</div>
                <div className="app-store__discount">-{discount}%</div>
            </>
        )}
        <div className="app-store__discount-price ">{discountPrice}</div>
    </div>
);

const GameInStoreRemove = ({ gameId, onRemove }) => {
    const { removeGameInStoreRequest } = useGameStoreActions();

    const handleRemove = () => {
        removeGameInStoreRequest(gameId, () => {
            onRemove(gameId);
        });
    };

    return (
        <div onClick={handleRemove} role="button" aria-label="Remove game">
            <img
                alt="Remove"
                className="app-store__trash-icon"
                src={trash}
            />
        </div>
    );
};


const GameStoresList = ({ stores, navigate }) => {
    const { isUserAdmin } = useAuth();

    const handleRemoveStore = () => {
        if (stores.length === 1) {
            navigate('/');
        } else {
            navigate(0);
        }
    };

    const renderedStores = useMemo(() => {
        return stores.map((store) => {
            const domain = new URL(store.url).hostname;
            const imagePath = `/assets/images/${domain}.png`;

            return (
                <div key={store.id} className="app-store__item-common">
                    <a
                        className="app-store__item"
                        href={store.url}
                        target="_blank"
                        rel="noopener noreferrer"
                    >
                        <img
                            src={imagePath}
                            alt={domain}
                            onError={(e) => (e.target.style.display = 'none')}
                        />
                        <ClientActivationType clientType={store.clientType} />
                        <div>{domain}</div>
                        <GameAvailability available={store.available} />
                        <StorePriceInfo
                            price={store.price}
                            discount={store.discount}
                            discountPrice={store.discountPrice}
                        />
                    </a>
                    {isUserAdmin() && <GameInStoreRemove gameId={store.id} onRemove={handleRemoveStore} />}
                </div>
            );
        });
    }, [stores]);

    return <div className="app-game__store-list">{renderedStores}</div>;
};

export default GameStoresList;
