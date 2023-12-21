import * as React from 'react';
import Message from './message';

export const pageSizesOptions = [
    { value: "25", label: "25" },
    { value: "50", label: "50" },
    { value: "75", label: "75" },
    { value: "100", label: "100" }
];

export const sortsOptions = [

    { value: "name-ASC", label: <Message string={'app.game.filter.sort.name'} /> },
    { value: "name-DESC", label: <Message string={'app.game.filter.sort.name.reverse'} /> },
    { value: "gamesInShop.price-ASC", label: <Message string={'app.game.filter.sort.price'} /> },
    { value: "gamesInShop.price-DESC", label: <Message string={'app.game.filter.sort.price.reverse'} /> }
];

export const ganresOptions = [
    { value: "action", label: <Message string={'app.game.genre.action'} /> },
    { value: "adventures", label: <Message string={'app.game.genre.adventures'} /> },
    { value: "casual", label: <Message string={'app.game.genre.casual'} /> },
    { value: "race", label: <Message string={'app.game.genre.race'} /> },
    { value: "rpg", label: <Message string={'app.game.genre.rpg'} /> },
    { value: "indie", label: <Message string={'app.game.genre.indie'} /> },
    { value: "online", label: <Message string={'app.game.genre.online'} /> },
    { value: "simulators", label: <Message string={'app.game.genre.simulators'} /> },
    { value: "strategies", label: <Message string={'app.game.genre.strategies'} /> },
    { value: "sport", label: <Message string={'app.game.genre.sport'} /> }
];

export const selectStyles = {
    control: styles => ({
        ...styles,
        backgroundColor: 'black',
        border: 0,
        boxShadow: 'none',
        marginTop: '6px',
    }),
    menuList: styles => ({
        ...styles,
        background: 'black',
    }),
    option: (styles, { isFocused, isSelected }) => ({
        ...styles,
        background: isFocused
            ? 'rgb(56, 113, 219)'
            : isSelected
                ? 'rgb(56, 113, 219)'
                : undefined,
    }),
    singleValue: styles => ({
        ...styles,
        color: 'white'
    })

}