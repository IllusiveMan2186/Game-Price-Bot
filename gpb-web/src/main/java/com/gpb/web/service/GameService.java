package com.gpb.web.service;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.Genre;

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
    Game getById(long gameId);

    /**
     * Get game by name
     *
     * @param name games name
     * @return game
     */
    Game getByName(String name);

    /**
     * Get game by url
     *
     * @param url game url from the store
     * @return game
     */
    Game getByUrl(String url);

    /**
     * Get games by genre
     *
     * @param genre    genre of the game
     * @param pageSize amount of elements on page
     * @param pageNum  page number
     * @return list of games
     */
    List<Game> getByGenre(Genre genre, int pageSize, int pageNum);

    /**
     * Create game
     *
     * @param game game that would be created
     * @return created game
     */
    Game create(Game game);
}
