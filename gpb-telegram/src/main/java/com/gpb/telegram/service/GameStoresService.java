package com.gpb.telegram.service;

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
     * Add to wishlist in store
     */
    void subscribeToGame(long gameId);

    /**
     * Remove from wishlist in store
     */
    void unsubscribeFromGame(long gameId);
}
