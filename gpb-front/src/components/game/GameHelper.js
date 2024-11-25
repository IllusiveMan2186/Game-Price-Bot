import * as React from 'react'
import Message from '../../util/message';
import GameContent from './GameContent';
import Pagination from './Pagination';
import { useTranslation } from "react-i18next";

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

export function ProductType(props) {
  if (props.type) {
    return (
      <div class="App-game-content-list-game-info-type">
        <Message string={'app.game.info.type.' + props.type.toLowerCase()} />
      </div>
    )
  }

}

export function Loading(props) {

  let image = require(`../../assets/images/load.png`)

  if (!props.games) {
    return (
      <img class="App-game-content-list-loading" src={image} on />
    )
  }

  return (<div>
    <div class="App-game-content-list">
      <GameContent games={props.games} />
    </div>
    <div class="App-game-content-fotter">
      <Pagination elementAmount={props.elementAmount} page={props.page}
        pageSize={props.pageSize} onPageClick={props.onPageClick} />
    </div>
  </div>
  )
}

export function Search(props) {

  const { t } = useTranslation();

  let placeholder = t('app.game.filter.search.title');

  return (
    <div class="App-game-content-header-search ">
      <input id="game-search-input-field" type="search" placeholder={placeholder}
        onChange={props.handleSearchChange} />
      <button id="game-search-button" onClick={props.handleSearch}>
        <Message string={'app.game.filter.search.button'} />
      </button>
    </div>
  )
}