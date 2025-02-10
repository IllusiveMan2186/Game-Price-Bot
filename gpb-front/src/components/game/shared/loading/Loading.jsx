import './Loading.css'

import Message from '@util/message';

export default function Loading(props) {
    let image = `/assets/images/load.png`
    return (
        <div className='app-content-loading'>
            <div ><Message string="app.game.search.wait" /></div>
            <img class="loading-img" src={image} on />
        </div>

    )
}