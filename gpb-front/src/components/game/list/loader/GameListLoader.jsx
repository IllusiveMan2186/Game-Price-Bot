import React from 'react';

import GameList from '../list/GameList';
import Pagination from '../pagination/Pagination';

import './GameListLoader.css';

const GameListLoader = ({ games, elementAmount, page, updateSearchParams, pageSize, reloadPage }) => {

    if (!games) {
        let image = `/assets/images/load.png`
        return (
            <img class="App-game-content-list-loading" src={image} on />
        )
    }
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