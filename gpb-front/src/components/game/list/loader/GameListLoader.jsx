import React from 'react';

import GameList from '@components/game/list/list/GameList';
import Pagination from '@components/game/list/pagination/Pagination';

import './GameListLoader.css';

const GameListLoader = ({ games, elementAmount, page, updateSearchParams, pageSize, reloadPage }) => {

    return (<div>
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