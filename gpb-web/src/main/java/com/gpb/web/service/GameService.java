package com.gpb.web.service;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInfoDto;
import com.gpb.web.bean.game.GameListPageDto;
import com.gpb.web.bean.game.Genre;

import java.math.BigDecimal;
import java.util.List;

/**
 * Class for handling game entity
 */
public interface GameService {

    /**
     * Get game by id
     *
     * @param gameId games id
     * @return game
     */
    GameInfoDto getById(long gameId);

    /**
     * Get game by name
     *
     * @param name games name
     * @return game
     */
    GameInfoDto getByName(String name);

    /**
     * Get game by url
     *
     * @param url game url from the store
     * @return game
     */
    GameInfoDto getByUrl(String url);

    /**
     * Get games by genre
     *
     * @param genre    genres of the game
     * @param pageSize amount of elements on page
     * @param pageNum  page number
     * @param minPrice minimal price
     * @param maxPrice maximal price
     * @return list of games with all amount
     */
    GameListPageDto getByGenre(List<Genre> genre, int pageSize, int pageNum, BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Create game
     *
     * @param game game that would be created
     * @return created game
     */
    Game create(Game game);
}
