package com.gpb.game.service;

import com.gpb.game.bean.game.Game;
import com.gpb.game.bean.game.GameInShop;

import java.util.List;

/**
 * CLass for handling work with game stores
 */
public interface GameStoresService {

    /**
     * Find game by name from all stores in system
     *
     * @param name name of the game
     * @return games
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

    /**
     * Check games from wishlist for changing
     *
     * @param gameInShops games that need to be checked
     * @return games that changed
     */
    List<GameInShop>  checkGameInStoreForChange(List<GameInShop> gameInShops);
}
