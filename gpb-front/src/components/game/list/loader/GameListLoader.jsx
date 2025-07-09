import React from 'react';

import { useSelector } from 'react-redux';

import GameList from '@components/game/list/loader/list/GameList';
import Pagination from '@components/game/list/loader/pagination/Pagination';
import Loading from '@components/game/shared/loading/Loading';
import Message from '@util/message';

import './GameListLoader.css';

const GameListLoader = ({ }) => {
    const { games, mode } = useSelector((state) => state.params);

    if (!games.length) {
        if (mode === "search") {
            return <Loading />
        } else {
            return <Message string="app.game.error.name.not.found" />
        }
    }

    if (games.length === 0) return <Message string="app.game.error.name.not.found" />

    return (
        <div>
            <div className="app-list">
                <GameList />
            </div>
            <div className="app-game-footer">
                <Pagination />
            </div>
        </div>
    )
}

export default GameListLoader;