import * as React from 'react';

import { request } from '../helpers/axios_helper';

import Select, { components } from "react-select";
import GameContent from './GameContent';
import Message from './Message';
import Pagination from './Pagination';

const pageSizes = [
    { value: "25", label: "25" },
    { value: "50", label: "50" },
    { value: "75", label: "75" },
    { value: "100", label: "100" }
];

const sortBy = [

    { value: "name-ASC", label: <Message string={'app.game.filter.sort.name'} /> },
    { value: "name-DESC", label: <Message string={'app.game.filter.sort.name.reverse'} /> },
    { value: "gamesInShop.price-ASC", label: <Message string={'app.game.filter.sort.price'} /> },
    { value: "gamesInShop.price-DESC", label: <Message string={'app.game.filter.sort.price.reverse'} /> }
];


const selectStyles = {
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

export default class GameNameSearch extends React.Component {

    searchParams = new URLSearchParams();
    games;

    constructor(props) {
        super(props);
        this.state = {
            s: "s",
            name: props.name,
            page: "1",
            sortBy: "",
            elementAmount: "",
            pageSize: pageSizes[0].label,
            isLoaded: false,
            isLoading: false,
            isFormChanged: false,
            getGame: props.getGameInfo
        };
        this.searchParams.append("pageSize", this.state.pageSize);
        this.searchParams.append("pageNum", this.state.page);
    };

    loadGames = async () => {
        this.searchParams.set("pageSize", this.state.pageSize);
        this.searchParams.set("pageNum", this.state.page);
        this.searchParams.set("sortBy", this.state.sortBy);

        this.setState({ isLoading: true })
        this.setState({ s: "/game/name/" + this.state.name + "?" + this.searchParams.toString() });
        request(
            "GET",
            "/game/name/" + this.state.name + "?" + this.searchParams.toString(),
        ).then(
            (response) => {
                this.games = response.data;
                this.setState({ elementAmount: response.data.elementAmount })
                this.setState({ isLoaded: true })
                this.setState({ isLoading: false })
                this.setState({ isFormChanged: false })
            }).catch(
                (error) => {
                }
            );
    }

    handleFilterButtonClick = (event) => {
        this.setState({ isLoaded: false })
    };


    handlePageSizeChange = (selectedOption) => {
        this.setState({ pageSize: selectedOption.value });
        this.setState({ page: 1 });
        this.setState({ isLoaded: false })
    };

    handleSortByChange = selectedOption => {
        this.setState({ sortBy: selectedOption.value });
        this.setState({ isLoaded: false })
    };

    handlePageChange = (selectedPage) => {
        this.setState({ page: selectedPage });
        this.setState({ isLoaded: false })
    };

    handleSearchChange = (event) => {
        this.setState({ name: event.target.value });
    };

    handleSearch = () => {
        this.setState({ isLoaded: false })
    };

    getGame = (gameId) => {
        this.state.getGame(gameId)
    };

    render() {

        !this.state.isLoaded && !this.state.isLoading && this.loadGames()
        return (
            <>
                <div class='App-game'>
                    <div class="App-game-search-content">
                        <div class="App-game-content-header ">
                            <div class="App-game-content-header-search ">
                            <input type="search" value={this.state.name} placeholder={<Message string={'app.game.filter.search.title'}/>} 
                                 onChange={this.handleSearchChange}/>
                                <button onClick={this.handleSearch}>
                                    <Message string={'app.game.filter.search.button'} />
                                </button>
                            </div>
                            <div class="App-game-content-header-sort ">
                                <Select
                                    classNamePrefix=''
                                    defaultValue={sortBy[0]}
                                    options={sortBy}
                                    onChange={this.handleSortByChange}
                                    styles={selectStyles}
                                    components={{
                                        IndicatorSeparator: () => null
                                    }}
                                    isSearchable={false}
                                />
                            </div>
                            <div class="App-game-content-header-sort ">

                                <Select
                                    defaultValue={pageSizes[0]}
                                    options={pageSizes}
                                    onChange={this.handlePageSizeChange}
                                    styles={selectStyles}
                                    components={{
                                        IndicatorSeparator: () => null
                                    }}
                                    isSearchable={false}
                                />
                            </div>
                        </div>
                        <div class="App-game-content-list ">
                            {this.state.isLoaded && <GameContent games={this.games} getGameI={this.getGame}/>}
                        </div>
                        <div class="App-game-content-fotter  ">
                            {this.state.isLoaded && <Pagination elementAmount={this.state.elementAmount} page={this.state.page}
                                pageSize={this.state.pageSize} onPageClick={this.handlePageChange} />}
                        </div>
                    </div>
                </div >
            </>
        );
    }
};