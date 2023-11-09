import * as React from 'react';

import { request } from '../helpers/axios_helper';
import { useLocation, useParams, useNavigate } from 'react-router-dom'

import Select from "react-select";
import GameContent from './GameContent';
import Message from './Message';
import Pagination from './Pagination';

const pageSizes = [
    { value: "25", label: "25" },
    { value: "50", label: "50" },
    { value: "75", label: "75" },
    { value: "100", label: "100" }
];

const sorts = [

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

export default function GameList(props) {
    let location = useLocation();
    const navigate = useNavigate();

    let { url = "" } = useParams();
    const [searchParams, setSearchParams] = React.useState(new URLSearchParams(url));

    const getParameterOrDefaultValue = (parameter, defaultValue) => {
        return parameter !== null ? parameter : defaultValue;
    }

    let minPrice = getParameterOrDefaultValue(searchParams.get("minPrice"), 0);
    let maxPrice = getParameterOrDefaultValue(searchParams.get("maxPrice"), 10000);
    let sortBy = getParameterOrDefaultValue(searchParams.get("sortBy"), "name-ASC");
    let page = getParameterOrDefaultValue(searchParams.get("pageNum"), 1);
    let pageSize = getParameterOrDefaultValue(searchParams.get("pageSize"), pageSizes[0].label);
    let { searchName = "" } = useParams();

    const [elementAmount, setElementAmount] = React.useState(0);
    const [games, setGames] = React.useState(null);
    const [isFormChanged, setFormChanged] = React.useState(false);
    const [priceError, setPriceError] = React.useState("");
    const [isFilterFormError, setFilterFormError] = React.useState(false);
    const [minPriceBuffer, setMinPrice] = React.useState(+minPrice);
    const [maxPriceBuffer, setMaxPrice] = React.useState(+maxPrice);
    const [name, setName] = React.useState(searchName);
    const [isList, setIsList] = React.useState(props.mode === "list");
    const [isSearch, setIsSearch] = React.useState(props.mode === "search");
    const [isUserGameList, setIsUserGameList] = React.useState(props.mode === "usersGames");

    console.info(props.mode)

    const parameterSetOrRemove = (parameter, value, defaultValue) => {
        if (value !== defaultValue) {
            searchParams.set(parameter, value);
        } else {
            searchParams.delete(parameter);
        }
    }

    const setParameters = () => {
        parameterSetOrRemove("pageSize", pageSize, "25")
        parameterSetOrRemove("pageNum", page, 1)
        parameterSetOrRemove("sortBy", sortBy, "name-ASC")
        if (isList) {
            parameterSetOrRemove("minPrice", minPrice, 0)
            parameterSetOrRemove("maxPrice", maxPrice, 10000)
        }
    }

    const handleFilterButtonClick = (event) => {
        minPrice = minPriceBuffer
        maxPrice = maxPriceBuffer
        reloadPage()
    };

    const handlePriceChange = (event) => {
        if (event.target.name === 'minPrice') {
            setMinPrice(event.target.value)
        } else {
            setMaxPrice(event.target.value)
        }
        setFormChanged(true)
        if ((event.target.name === 'minPrice' && maxPriceBuffer >= +event.target.value)
            || (event.target.name === 'maxPrice' && +event.target.value >= minPriceBuffer)) {
            setFilterFormError(false)
            setPriceError("")
        } else {
            setFilterFormError(true)
            setPriceError(<Message string={'app.game.error.price'} />)
        }
    };

    const handlePageSizeChange = (selectedOption) => {
        pageSize = selectedOption.value
        page = 1
        reloadPage()
    };

    const handleSortByChange = selectedOption => {
        sortBy = selectedOption.value
        reloadPage()
    };

    const handlePageChange = (selectedPage) => {
        page = selectedPage
        reloadPage()
    };

    const handleGenreChange = (event) => {
        if (event.target.checked) {
            searchParams.append("genre", event.target.value.toUpperCase());
        } else {
            searchParams.delete("genre", event.target.value.toUpperCase());
        }
        page = 1
        setFormChanged(true)

    };

    const handleSearchChange = (event) => {
        setName(event.target.value)
    };

    const handleSearch = () => {
        if (isSearch) {
            navigate("/search/" + name + "/" + searchParams.toString())
            navigate(0);
        } else {
            navigate("/search/" + name + "/")
            navigate(0);
        }
    };

    const isFilterFormReadyToAccept = () => {
        return !isFilterFormError && isFormChanged
    }

    const reloadPage = () => {
        setParameters()

        switch (props.mode) {
            case "usersGames":
                navigate("/user/games/" + searchParams.toString())
                break;
            case "search":
                navigate("/search/" + name + "/" + searchParams.toString())
                break;
            default:
                navigate("/games/" + searchParams.toString())
                break;
        }
        navigate(0);
    }

    const isChecked = (genre) => {
        return searchParams.has("genre", genre.toUpperCase())
    }

    const findArrayElementByValue = (array, value) => {
        return array.find((element) => {
            return element.value === value;
        })
    }

    const handleEvent = () => {
        navigate(0)
    };


    setParameters()
    React.useEffect(() => {
        window.addEventListener("popstate", handleEvent);
        switch (props.mode) {
            case "usersGames":
                request(
                    "GET",
                    "/game/user/games?" + searchParams.toString(),
                ).then(
                    (response) => {
                        setElementAmount(response.data.elementAmount)
                        setGames(response.data.games);
                        setFormChanged(false)
                    })
                break;
            case "search":
                request(
                    "GET",
                    "/game/name/" + name + "?" + searchParams.toString(),
                ).then(
                    (response) => {
                        setElementAmount(response.data.elementAmount)
                        setGames(response.data.games);
                        setFormChanged(false)
                    })
                break;
            default:
                request(
                    "GET",
                    "/game/genre?" + searchParams.toString(),
                ).then(
                    (response) => {
                        setElementAmount(response.data.elementAmount)
                        setGames(response.data.games);
                        setFormChanged(false)
                    })
                break;
        }
    }, []);

    if (!games) return null;

    return (
        <>
            <div class='App-game'>
                {isList && <Filter minPrice={minPrice} maxPrice={maxPrice} handlePriceChange={handlePriceChange} handleGenreChange={handleGenreChange}
                    handleFilterButtonClick={handleFilterButtonClick} isFilterFormReadyToAccept={isFilterFormReadyToAccept} isChecked={isChecked}
                    priceError={priceError} ganres={ganres} />}

                <div className={isList ? "App-game-content" : "App-game-search-content"}>
                    <div class="App-game-content-header ">
                        {!isUserGameList && <Search handleSearchChange={handleSearchChange} handleSearch={handleSearch} />}
                        <div class="App-game-content-header-sort ">
                            <Select
                                classNamePrefix=''
                                defaultValue={findArrayElementByValue(sorts, sortBy)}
                                options={sorts}
                                onChange={handleSortByChange}
                                styles={selectStyles}
                                components={{
                                    IndicatorSeparator: () => null
                                }}
                                isSearchable={false}
                            />
                        </div>
                        <div class="App-game-content-header-sort ">

                            <Select
                                defaultValue={findArrayElementByValue(pageSizes, pageSize)}
                                options={pageSizes}
                                onChange={handlePageSizeChange}
                                styles={selectStyles}
                                components={{
                                    IndicatorSeparator: () => null
                                }}
                                isSearchable={false}
                            />
                        </div>
                    </div>
                    <div class="App-game-content-list ">
                        <GameContent games={games} />
                    </div>
                    <div class="App-game-content-fotter  ">
                        <Pagination elementAmount={elementAmount} page={page}
                            pageSize={pageSize} onPageClick={handlePageChange} />
                    </div>
                </div>
            </div >
        </>
    );

};

function Search(props) {

    return (
        <div class="App-game-content-header-search ">
            <input type="search" placeholder={<Message string={'app.game.filter.search.title'} />}
                onChange={props.handleSearchChange} />
            <button onClick={props.handleSearch}>
                <Message string={'app.game.filter.search.button'} />
            </button>
        </div>
    )
}

function Filter(props) {

    return (
        <aside class="col-lg-3 App-game-filter">
            <div class="App-game-filter-title"><Message string={'app.game.filter.title'} /></div>
            <div class="App-game-filter-subdiv">
                <div class=" App-game-filter-section">
                    <div class="ocf-filter-header" data-ocf="expand">
                        <i class="ocf-mobile ocf-icon ocf-arrow-long ocf-arrow-left"></i>
                        <div class="App-game-filter-title">
                            <Message string={'app.game.filter.price.title'} />
                        </div>
                    </div>
                    <div class="">
                        <div class="App-game-filter-price-inputs">

                            <input type="number" name="minPrice" defaultValue={props.minPrice}
                                onChange={props.handlePriceChange} />
                            <span >-</span>
                            <input type="number" name="maxPrice" defaultValue={props.maxPrice}
                                onChange={props.handlePriceChange} />
                            <span> ₴</span>

                        </div>
                        <span className='Error'>{props.priceError}</span>
                    </div>
                </div>


                <div class=" App-game-filter-section">
                    <div class="App-game-filter-title"><Message string={'app.game.filter.genre.title'} /></div>
                    <div class="App-game-filter-genre">
                        {
                            ganres.map(ganre => {
                                return (
                                    <label class="App-game-filter-genre-button"  >
                                        <input type="checkbox" class="App-game-filter-genre-button-checkbox"
                                            value={ganre.value} onChange={props.handleGenreChange}
                                            defaultChecked={props.isChecked(ganre.value)}></input>
                                        <span class="App-game-filter-genre-button-text">{ganre.label}</span>
                                    </label>)
                            })
                        }

                    </div>
                </div>
                <button type="submit" className="btn btn-primary btn-block mb-3"
                    disabled={!props.isFilterFormReadyToAccept()} onClick={props.handleFilterButtonClick}>
                    <Message string={'app.game.filter.accept.button'} /> </button>
            </div>
        </aside >
    )
}