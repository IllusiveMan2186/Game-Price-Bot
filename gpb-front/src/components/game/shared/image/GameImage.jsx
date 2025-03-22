import config from "@root/config";

function sanitizeFilename(filename) {
  return filename.replace(/[:/]/g, '_');
}

export default function GameImage(props) {
  return (
    <img 
      className={props.className} 
      src={`${config.BACKEND_SERVICE_URL}/game/image/${sanitizeFilename(props.gameName)}`} 
      alt={props.gameName}
    />
  );
}
