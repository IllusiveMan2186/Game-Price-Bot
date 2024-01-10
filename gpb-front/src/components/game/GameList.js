import * as React from 'react';
import '../../styles/gameList.css';
import { getGamesRequest } from '../../request/gameRequests';
import * as constants from '../../util/constants';
import { useParams, useNavigate } from 'react-router-dom'
import { Loading, Search } from './GameHelper';
import Select from "react-select";
import GameListFilter from './GameListFilter';

export default function GameList(props) {
    const navigate = useNavigate();

    let { url = "" } = useParams();
    const [searchParams, setSearchParams] = React.useState(new URLSearchParams(url));

    const getParameterOrDefaultValue = (parameter, defaultValue) => {
        return parameter !== null ? parameter : defaultValue;
    }

    let sortBy = getParameterOrDefaultValue(searchParams.get("sortBy"), "name-ASC");
    let page = getParameterOrDefaultValue(searchParams.get("pageNum"), 1);
    let pageSize = getParameterOrDefaultValue(searchParams.get("pageSize"), constants.pageSizesOptions[0].label);
    let { searchName = "" } = useParams();

    const [elementAmount, setElementAmount] = React.useState(0);
    const [games, setGames] = React.useState(null);
    const [name, setName] = React.useState(searchName);

    const parameterSetOrRemove = (parameter, value, defaultValue) => {
        if (value !== defaultValue) {
            console.info(parameter + " " + value + " " + defaultValue)
            searchParams.set(parameter, value);
        } else {
            searchParams.delete(parameter);
        }
    }

    const setParameters = () => {
        parameterSetOrRemove("pageSize", pageSize, "25")
        parameterSetOrRemove("pageNum", page, 1)
        parameterSetOrRemove("sortBy", sortBy, "name-ASC")
    }

    const setPage = (value) => {
        page = value
    }

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

    const handleSearchChange = (event) => {
        setName(event.target.value)
    };

    const handleSearch = () => {
        if (props.mode === "search") {
            navigate("/search/" + name + "/" + searchParams.toString())
            navigate(0);
        } else {
            navigate("/search/" + name + "/")
            navigate(0);
        }
    };

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

    const findArrayElementByValue = (array, value) => {
        return array.find((element) => {
            return element.value === value;
        })
    }

    const getSearchParametrs = () => {
        switch (props.mode) {
            case "usersGames":
                return "/game/user/games?" + searchParams.toString()
            case "search":
                return "/game/name/" + name + "?" + searchParams.toString()
            default:
                return "/game/genre?" + searchParams.toString()
        }
    };

    setParameters()
    React.useEffect(() => {
        getGamesRequest(getSearchParametrs(), setElementAmount, setGames, navigate)
    }, []);

    return (
        <>
            <div class='App-game'>
                {(props.mode === "list") && <GameListFilter getParameterOrDefaultValue={getParameterOrDefaultValue} searchParams={searchParams} reloadPage={reloadPage}
                    setPage={setPage} parameterSetOrRemove={parameterSetOrRemove} />}

                <div className={(props.mode === "list") ? "App-game-content" : "App-game-search-content"}>
                    <div class="App-game-content-header ">
                        {!(props.mode === "usersGames") && <Search handleSearchChange={handleSearchChange} handleSearch={handleSearch} />}
                        <div class="App-game-content-header-sort ">
                            <Select
                                classNamePrefix=''
                                defaultValue={findArrayElementByValue(constants.sortsOptions, sortBy)}
                                options={constants.sortsOptions}
                                onChange={handleSortByChange}
                                styles={constants.selectStyles}
                                components={{
                                    IndicatorSeparator: () => null
                                }}
                                isSearchable={false}
                            />
                        </div>
                        <div class="App-game-content-header-sort ">

                            <Select
                                defaultValue={findArrayElementByValue(constants.pageSizesOptions, pageSize)}
                                options={constants.pageSizesOptions}
                                onChange={handlePageSizeChange}
                                styles={constants.selectStyles}
                                components={{
                                    IndicatorSeparator: () => null
                                }}
                                isSearchable={false}
                            />
                        </div>
                    </div>
                    <Loading games={games} elementAmount={elementAmount} page={page}
                        pageSize={pageSize} onPageClick={handlePageChange} />
                </div>
            </div >
        </>
    );

};
