import React, { useState, useMemo } from 'react';
import { GameAvailability } from '@components/game/shared/availability/GameAvailability';
import { isUserAdmin } from '@util/userDataUtils';
import { removeGameInStoreRequest } from '@services/gameRequests';
import trash from '@assets/images/trash.png';
import './GameStoresList.css';

const ClientActivationType = ({ clientType }) => {
    if (!clientType) return null;
    const imagePath = `/assets/images/${clientType.toLowerCase()}.png`;

    return <img src={imagePath} alt={clientType} onError={(e) => (e.target.style.display = 'none')} />;
};

const StorePriceInfo = ({ price, discount, discountPrice }) => (
    <div className="App-game-page-info-storeList-store-price-section">
        {discount > 0 && discountPrice < price && (
            <>
                <div className="App-game-page-info-storeList-store-price">{price}</div>
                <div className="App-game-page-info-storeList-store-discount">-{discount}%</div>
            </>
        )}
        <div className="App-game-page-info-storeList-store-discountPrice">{discountPrice}</div>
    </div>
);

const GameInStoreRemove = ({ gameId, onRemove }) => {
    const handleRemove = () => {
        removeGameInStoreRequest(gameId, () => {
            onRemove(gameId);
        });
    };

    return (
        <div onClick={handleRemove} role="button" aria-label="Remove game">
            <img
                alt="Remove"
                className="App-game-page-info-storeList-store-trash-icon"
                src={trash}
            />
        </div>
    );
};


const GameStoresList = ({ stores: initialStores, navigate }) => {
    const [stores, setStores] = useState(initialStores);

    const handleRemoveStore = (storeId) => {
        setStores((prevStores) => {
            const newStores = prevStores.filter((store) => store.id !== storeId);
            if (newStores.length === 0) {
                // Wait 500ms before navigating
                setTimeout(() => {
                    navigate('/');
                }, 100);
            }
            return newStores;
        });
    };

    const renderedStores = useMemo(() => {
        return stores.map((store) => {
            const domain = new URL(store.url).hostname;
            const imagePath = `/assets/images/${domain}.png`;

            return (
                <div key={store.id} className="App-game-page-info-storeList-store-common">
                    <a
                        className="App-game-page-info-storeList-store"
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

    return <div className="App-game-page-info-storeList">{renderedStores}</div>;
};

export default GameStoresList;
