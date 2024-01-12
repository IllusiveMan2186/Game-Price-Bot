import * as React from 'react';
import '../../styles/gameList.css';
import * as constants from '../../util/constants';
import Message from '../../util/message';

export default function GameListFilter(props) {

    const [isFormChanged, setFormChanged] = React.useState(false);
    const [priceError, setPriceError] = React.useState("");
    const [isFilterFormError, setFilterFormError] = React.useState(false);
    const [minPrice, setMinPrice] = React.useState(+props.getParameterOrDefaultValue(props.searchParams.get("minPrice"), 0));
    const [maxPrice, setMaxPrice] = React.useState(+props.getParameterOrDefaultValue(props.searchParams.get("maxPrice"), 10000));

    const handleFilterButtonClick = (event) => {
        props.parameterSetOrRemove("minPrice", minPrice, 0)
        props.parameterSetOrRemove("maxPrice", maxPrice, 10000)
        props.reloadPage()
    };

    const handlePriceChange = (event) => {
        if (event.target.name === 'minPrice') {
            setMinPrice(event.target.value)
        } else {
            setMaxPrice(event.target.value)
        }
        setFormChanged(true)
        if ((event.target.name === 'minPrice' && maxPrice >= +event.target.value)
            || (event.target.name === 'maxPrice' && +event.target.value >= minPrice)) {
            setFilterFormError(false)
            setPriceError("")
        } else {
            setFilterFormError(true)
            setPriceError(<Message string={'app.game.error.price'} />)
        }
    };

    const isChecked = (genre, field, isNotExcludedFieldType) => {
        return props.searchParams.has(field, genre.toUpperCase()) === isNotExcludedFieldType
    }

    const isFilterFormReadyToAccept = () => {
        return !isFilterFormError && isFormChanged
    }

    const handleCheckboxChange = (event, isNotExcludedFieldType) => {
        if ((event.target.checked && isNotExcludedFieldType) || (!event.target.checked && !isNotExcludedFieldType)) {
            props.searchParams.append(event.target.name, event.target.value.toUpperCase());
        } else {
            props.searchParams.delete(event.target.name, event.target.value.toUpperCase());
        }
        props.setPage(1)
        setFormChanged(true)

    };

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

                            <input type="number" name="minPrice" defaultValue={minPrice}
                                onChange={handlePriceChange} />
                            <span >-</span>
                            <input type="number" name="maxPrice" defaultValue={maxPrice}
                                onChange={handlePriceChange} />
                            <span> ₴</span>

                        </div>
                        <span className='Error'>{priceError}</span>
                    </div>
                </div>

                <div class=" App-game-filter-section">
                    <div class="App-game-filter-title"><Message string={'app.game.filter.genre.title'} /></div>
                    <div class="App-game-filter-genre">
                        {
                            constants.ganresOptions.map(ganre => {
                                return (
                                    <label class="App-game-filter-genre-button"  >
                                        <input type="checkbox" class="App-game-filter-genre-button-checkbox"
                                            name='genre' value={ganre.value} onChange={(event) => handleCheckboxChange(event, true)}
                                            defaultChecked={isChecked(ganre.value, "genre", true)}></input>
                                        <span class="App-game-filter-genre-button-text">{ganre.label}</span>
                                    </label>)
                            })
                        }

                    </div>
                </div>

                <div class=" App-game-filter-section">
                    <div class="App-game-filter-title"><Message string={'app.game.info.type'} /></div>
                    <div class="App-game-filter-genre">
                        {
                            constants.productTypesOptions.map(type => {
                                return (
                                    <label class="App-game-filter-genre-button"  >
                                        <input type="checkbox" class="App-game-filter-genre-button-checkbox"
                                            name='type' value={type.value} onChange={(event) => handleCheckboxChange(event, false)}
                                            defaultChecked={isChecked(type.value, "type", false)}></input>
                                        <span class="App-game-filter-genre-button-text">{type.label}</span>
                                    </label>)
                            })
                        }

                    </div>
                </div>

                <button type="submit" className="btn btn-primary btn-block mb-3"
                    disabled={!isFilterFormReadyToAccept()} onClick={handleFilterButtonClick}>
                    <Message string={'app.game.filter.accept.button'} /> </button>
            </div>
        </aside >
    )
}