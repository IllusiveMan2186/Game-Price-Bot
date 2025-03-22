import Message from '@util/message';

import './GameGenres.css';

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

export default GameGenres;