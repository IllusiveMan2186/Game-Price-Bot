package com.gpb.web.service;

import com.gpb.web.bean.Game;

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
    Game findGameByName(String name);

    /**
     * Find game by url from all stores in system
     *
     * @param url url of the game
     * @return game
     */
    Game findGameByUrl(String url);
}
