package com.gpb.web.service;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;

import java.util.List;

/**
 * CLass for handling work with game stores
 */
public interface GameStoresService {

    /**
     * Find game by name from all stores in system
     *
     * @param name name of the game
     * @return games ids
     */
    List<Long> findGameByName(String name);

    /**
     * Find game by url from all stores in system
     *
     * @param url url of the game
     * @return game id
     */
    Long findGameByUrl(String url);

    /**
     * Add to wishlist in store
     */
    void subscribeToGame(long gameId);

    /**
     * Remove from wishlist in store
     */
    void unsubscribeFromGame(long gameId);

}
