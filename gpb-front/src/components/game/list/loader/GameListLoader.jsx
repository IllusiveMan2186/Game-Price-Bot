import React from 'react';

import GameList from '@components/game/list/list/GameList';
import Pagination from '@components/game/list/pagination/Pagination';
import Loading from '@components/game/shared/loading/Loading';
import Message from '@util/message';

import './GameListLoader.css';

const GameListLoader = ({ games, elementAmount, page, mode, updateSearchParams, pageSize, reloadPage }) => {

    if (!games) {
        if (mode === "search") {
            return <Loading />
        } else {
            return <Message string="app.game.error.name.not.found" />
        }
    }

    if (games.length === 0) return <Message string="app.game.error.name.not.found" />

    return (
        <div>
            <div class="App-game-content-list">
                <GameList games={games} />
            </div>
            <div class="App-game-content-fotter">
                <Pagination
                    elementAmount={elementAmount}
                    page={page}
                    pageSize={pageSize}
                    reloadPage={reloadPage}
                    updateSearchParams={updateSearchParams}
                />
            </div>
        </div>
    )
}

export default GameListLoader;