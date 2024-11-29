import './Loading.css'

export default function Loading(props) {
    let image = `/assets/images/load.png`
    return (
        <img class="App-content-loading" src={image} on />
    )
  }