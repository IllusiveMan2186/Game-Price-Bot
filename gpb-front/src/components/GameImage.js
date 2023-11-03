import * as React from 'react'
import defaultImage from '../img/defaultImage.jpg';
import Message from './Message';


export function GameImage(props) {
    let image 
    try {
        image = require(`../img/${props.gameName}.jpg`)
      } catch {
        image = defaultImage
      }
       
      return (<img class={props.className} src={image} on ></img>);
}

export function GameAvailability(props) {
  return (
      <div class={props.available ? "App-game-content-list-game-info-available" 
      : "App-game-content-list-game-info-available not-available"}>
          {props.available ? <Message string={'app.game.is.available'} />
              : <Message string={'app.game.not.available'} />}
      </div>
  )
}