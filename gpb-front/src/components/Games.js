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

const ganres = [
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

const selectStyles = {
    control: styles => ({
        ...styles,
        backgroundColor: 'black',
        border: 0,
        // This line disable the blue border
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

export default class Games extends React.Component {

    searchParams = new URLSearchParams();
    games;

    constructor(props) {
        super(props);
        this.state = {
            minPrice: "0",
            maxPrice: "10000",
            price: "0",
            pp: "0",
            sortBy: "",
            elementAmount: "",
            page: "1",
            pageSize: pageSizes[0].label,
            priceError: "",
            isLoaded: false,
            isLoading: false,
            isFormChanged: false,
            isFilterFormError: false,
        };
        this.searchParams.append("pageSize", this.state.pageSize);
        this.searchParams.append("pageNum", this.state.page);
    };

    loadGames = async () => {
        this.searchParams.set("pageSize", this.state.pageSize);
        this.searchParams.set("pageNum", this.state.page);
        this.searchParams.set("minPrice", this.state.minPrice);
        this.searchParams.set("maxPrice", this.state.maxPrice);
        this.searchParams.set("sortBy", this.state.sortBy);

        this.setState({ isLoading: true })
        const response = await request(
            "GET",
            "/game/genre?" + this.searchParams.toString(),
        ).then(
            (response) => {
                this.games = response.data.games;
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

    handlePriceChange = (event) => {
        this.setState({ [event.target.name]: event.target.value })
        this.setState({ isFormChanged: true })
        let name = 'maxPrice'
        if ((event.target.name === 'minPrice' && +this.state.maxPrice >= +event.target.value)
            || (event.target.name === 'maxPrice' && +event.target.value >= +this.state.minPrice)) {
            this.setState({ isFilterFormError: false })
            this.setState({ priceError: "" })
        } else {
            this.setState({ isFilterFormError: true })
            this.setState({ priceError: <Message string={'app.game.error.price'} /> })
        }


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

    handleGenreCHange = (event) => {
        if (event.target.checked) {
            this.searchParams.append("genre", event.target.value.toUpperCase());
            this.setState({ confirmPasswordError: "yes" })
        } else {
            this.searchParams.delete("genre", event.target.value.toUpperCase());
            this.setState({ confirmPasswordError: "no" })
        }
        this.setState({ page: 1 });
        this.setState({ isFormChanged: true })
    };

    isFilterFormReadyToAccept = () => {
        return !this.state.isFilterFormError && this.state.isFormChanged
    }

    render() {

        !this.state.isLoaded && !this.state.isLoading && this.loadGames()
        return (
            <>
                <div class='App-game'>
                    <aside class="col-lg-3 App-game-filter">
                        <div class="App-game-filter-title"><Message string={'app.game.filter.title'} /></div>
                        <div class="App-game-filter-subdiv">
                            <div class=" App-game-filter-section">
                                <div class="ocf-filter-header" data-ocf="expand">
                                    <i class="ocf-mobile ocf-icon ocf-arrow-long ocf-arrow-left"></i>
                                    <div class="App-game-filter-title"><Message string={'app.game.filter.price.title'} /></div>
                                </div>
                                <div class="">
                                    <div class="App-game-filter-price-inputs">

                                        <input type="number" name="minPrice" defaultValue={this.state.minPrice} onChange={this.handlePriceChange} />
                                        <span >-</span>
                                        <input type="number" name="maxPrice" defaultValue={this.state.maxPrice} onChange={this.handlePriceChange} />
                                        <span> ₴</span>

                                    </div>
                                    <span className='Error'>{this.state.priceError}</span>
                                </div>
                            </div>


                            <div class=" App-game-filter-section">
                                <div class="App-game-filter-title"><Message string={'app.game.filter.genre.title'} /></div>
                                <div class="App-game-filter-genre">
                                    {
                                        ganres.map(ganre => {
                                            return (<label class="App-game-filter-genre-button"  >
                                                <input type="checkbox" class="App-game-filter-genre-button-checkbox" value={ganre.value} onChange={this.handleGenreCHange}></input>
                                                <span class="App-game-filter-genre-button-text">{ganre.label}</span>
                                            </label>)
                                        })
                                    }

                                </div>
                            </div>
                            <button type="submit" className="btn btn-primary btn-block mb-3" disabled={!this.isFilterFormReadyToAccept()} onClick={this.handleFilterButtonClick}> <Message string={'app.game.filter.accept.button'} /> </button>
                        </div>
                    </aside >

                    <div class="App-game-content">
                        <div class="App-game-content-header ">
                            <div class="App-game-content-header-search ">
                                <input type="search" placeholder={<Message string={'app.game.filter.search.title'} />} />
                                <button><Message string={'app.game.filter.search.button'} /></button>
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
                            {this.state.isLoaded && <GameContent games={this.games} />}
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