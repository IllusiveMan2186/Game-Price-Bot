import React, { useEffect, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../../styles/gameInfo.css';
import Message from '../../util/message';
import { GameImage, GameAvailability, ProductType } from './GameHelper';
import {
  getGameRequest,
  subscribeForGameRequest,
  unsubscribeForGameRequest,
  removeGameRequest,
} from '../../request/gameRequests';
import { isUserAdmin, isUserAuth } from '../../util/axios_helper';

const GameInfo = () => {
  const [game, setGame] = useState(null);
  const navigate = useNavigate();
  const { gameId } = useParams();

  useEffect(() => {
    const fetchGame = async () => {
      await getGameRequest(gameId, setGame, navigate);
    };
    fetchGame();
  }, [gameId, navigate]);

  if (!game) return <div>Loading...</div>;

  return (
    <div className="App-game">
      <div className="App-game-page-template">
        <div className="App-game-page">
          <div className="App-game-page-image">
            <GameImage className="App-game-content-list-game-info-img" gameName={game.name} />
          </div>
          <div className="App-game-page-info-half">
            <GameDetails game={game} gameId={gameId} navigate={navigate} />
          </div>
        </div>
      </div>
    </div>
  );
};

const GameDetails = ({ game, gameId, navigate }) => (
  <div className="App-game-page-info">
    <h1 className="App-game-page-info-title">{game.name}</h1>
    <div className="App-game-page-info-common">
      <GamePricing game={game} />
      <GameGenres genres={game.genres} />
    </div>
    <SubscribeButton isSubscribed={game.userSubscribed} gameId={gameId} navigate={navigate} />
    {isUserAdmin() && <RemoveButton gameId={gameId} navigate={navigate} />}
    <GameStoresList stores={game.gamesInShop} />
  </div>
);

const GamePricing = ({ game }) => (
  <div className="App-game-page-info-common-price">
    <ProductType type={game.type} />
    <GameAvailability available={game.available} />
    <div className="App-game-content-list-game-info-price">
      {game.minPrice} - {game.maxPrice} ₴
    </div>
  </div>
);

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

const GameStoresList = ({ stores }) => (
  <div className="App-game-page-info-storeList">
    {stores.map((store) => {
      const domain = new URL(store.url).hostname;
      const image = require(`../../assets/images/${domain}.png`);
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

const StorePriceInfo = ({ price, discount, discountPrice }) => (
  <div className="App-game-page-info-storeList-store-price-section">
    <div className="App-game-page-info-storeList-store-price">{price}</div>
    <div className="App-game-page-info-storeList-store-discount">-{discount}%</div>
    <div className="App-game-page-info-storeList-store-discountPrice">{discountPrice}</div>
  </div>
);

const SubscribeButton = ({ isSubscribed, gameId, navigate }) => {
  const handleSubscribe = useCallback(() => {
    if (isSubscribed) {
      unsubscribeForGameRequest(gameId, navigate);
    } else {
      subscribeForGameRequest(gameId, navigate);
    }
  }, [isSubscribed, gameId, navigate]);

  return (
    <div className="App-game-page-info-subscribe">
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

const RemoveButton = ({ gameId, navigate }) => (
  <button
    type="button"
    className="btn btn-danger btn-block mb-3 App-game-page-info-remove"
    onClick={() => removeGameRequest(gameId, navigate)}
  >
    <Message string="app.game.info.remove" />
  </button>
);

const ClientActivationType = ({ clientType }) => {
  if (!clientType) return null;
  const image = require(`../../assets/images/${clientType.toLowerCase()}.png`);
  return <img src={image} alt={clientType} />;
};

export default GameInfo;
