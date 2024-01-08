import * as React from 'react'
import defaultImage from '../../img/defaultGameImage.jpg';
import Message from '../../util/message';
import GameContent from './GameContent';
import Pagination from './Pagination';

export function GameImage(props) {
  return (<img class={props.className} src={`http://localhost:8080/game/image/${props.gameName}`} on ></img>);
}

export function GameAvailability(props) {
  return (
    <div class={props.available ? "App-game-content-list-game-info-available" : "App-game-content-list-game-info-available not-available"}>
      {props.available ? <Message string={'app.game.is.available'} /> : <Message string={'app.game.not.available'} />}
    </div>
  )
}

export function Loading(props) {

  let image = require(`../../img/load.png`)

  if (!props.games) {
    return (
      <img class="App-game-content-list-loading" src={image} on />
    )
  }

  return (<div>
    <div class="App-game-content-list ">
      <GameContent games={props.games} />
    </div>
    <div class="App-game-content-fotter  ">
      <Pagination elementAmount={props.elementAmount} page={props.page}
        pageSize={props.pageSize} onPageClick={props.onPageClick} />
    </div>
  </div>
  )
}

export function Search(props) {

  return (
    <div class="App-game-content-header-search ">
      <input type="search" placeholder={<Message string={'app.game.filter.search.title'} />}
        onChange={props.handleSearchChange} />
      <button onClick={props.handleSearch}>
        <Message string={'app.game.filter.search.button'} />
      </button>
    </div>
  )
}