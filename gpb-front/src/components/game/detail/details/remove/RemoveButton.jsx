import Message from '@util/message';
import { useGameActions, } from '@hooks/game/useGameActions';

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

export default RemoveButton;