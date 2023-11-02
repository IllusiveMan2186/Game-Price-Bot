import * as React from 'react'
import defaultImage from '../img/defaultImage.jpg';


export default function GameImage(props) {
    let image 
    try {
        image = require(`../img/${props.gameName}.jpg`)
      } catch {
        image = defaultImage
      }
       
      return (<img class={props.className} src={image} on ></img>);
}