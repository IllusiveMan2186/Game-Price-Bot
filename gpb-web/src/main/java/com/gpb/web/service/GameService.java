package com.gpb.web.service;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameDto;
import com.gpb.web.bean.game.GameInfoDto;
import com.gpb.web.bean.game.GameListPageDto;
import com.gpb.web.bean.game.Genre;
import org.springframework.data.domain.Sort;

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
     * @param pageSize amount of elements on page
     * @param pageNum  page number
     * @param sort sort parameters
     * @return game
     */
    List<GameDto> getByName(final String name, final int pageSize, final int pageNum, Sort sort);

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
     * @param sort sort parameters
     * @return list of games with all amount
     */
    GameListPageDto getByGenre(List<Genre> genre, int pageSize, int pageNum, BigDecimal minPrice, BigDecimal maxPrice,
                               Sort sort);

    /**
     * Create game
     *
     * @param game game that would be created
     * @return created game
     */
    Game create(Game game);
}
