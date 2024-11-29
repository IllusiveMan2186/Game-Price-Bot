import React from 'react';
import { GameAvailability } from '../../shared/availability/GameAvailability';
import './GameStoresList.css'

const ClientActivationType = ({ clientType }) => {
    if (!clientType) return null;
    const image = `/assets/images/${clientType.toLowerCase()}.png`;
    return <img src={image} alt={clientType} />;
};

const StorePriceInfo = ({ price, discount, discountPrice }) => (
    <div className="App-game-page-info-storeList-store-price-section">
        <div className="App-game-page-info-storeList-store-price">{price}</div>
        <div className="App-game-page-info-storeList-store-discount">-{discount}%</div>
        <div className="App-game-page-info-storeList-store-discountPrice">{discountPrice}</div>
    </div>
);

const GameStoresList = ({ stores }) => (
    <div className="App-game-page-info-storeList">
        {stores.map((store) => {
            const domain = new URL(store.url).hostname;
            const image = `/assets/images/${domain}.png`;
            return (
                <a
                    key={store.url}
                    className="App-game-page-info-storeList-store"
                    href={store.url}
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    <img src={image} alt={domain} />
                    <ClientActivationType clientType={store.clientType} />
                    <div>{domain}</div>
                    <GameAvailability available={store.available} />
                    <StorePriceInfo price={store.price} discount={store.discount} discountPrice={store.discountPrice} />
                </a>
            );
        })}
    </div>
);

export default GameStoresList;
