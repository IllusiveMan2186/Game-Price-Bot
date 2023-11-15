package com.gpb.web.service;

import com.gpb.web.bean.game.Game;

import java.util.List;

/**
 * CLass for handling work with game stores
 */
public interface GameStoresService {

    /**
     * Find game by name from all stores in system
     *
     * @param name name of the game
     * @return game
     */
    List<Game> findGameByName(String name);

    /**
     * Find game by url from all stores in system
     *
     * @param url url of the game
     * @return game
     */
    Game findGameByUrl(String url);

    /**
     * Add to wishlist in store
     *
     * @param game game
     */
    void subscribeToGame(Game game);

    /**
     * Remove from wishlist in store
     *
     * @param game game
     */
    void unsubscribeFromGame(Game game);
}
