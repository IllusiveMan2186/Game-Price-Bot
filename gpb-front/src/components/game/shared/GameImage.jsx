function sanitizeFilename(filename) {
  return filename.replace(/[:/]/g, '_');
}

export default function GameImage(props) {
  return (<img class={props.className} src={`http://localhost:8080/game/image/${sanitizeFilename(props.gameName)}`} on ></img>);
}
