
import { useAuth } from "@contexts/AuthContext";

import { CommonGameInfo } from '@components/game/shared/info/CommonGameInfo';
import GameStoresList from '@components/game/detail/details/stores/list/GameStoresList';
import AddGameInStore from '@components/game/detail/details/stores/adding/AddGameInStore';
import SubscribeButton from '@components/game/detail/details/subscribe/SubscribeButton';
import GameGenres from '@components/game/detail/details/genres/GameGenres';
import RemoveButton from '@components/game/detail/details/remove/RemoveButton';

import './GameDetails.css';

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
