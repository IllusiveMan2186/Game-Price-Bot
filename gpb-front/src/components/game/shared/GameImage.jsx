export default function GameImage(props) {
    return (<img class={props.className} src={`http://localhost:8080/game/image/${props.gameName}`} on ></img>);
  }
  