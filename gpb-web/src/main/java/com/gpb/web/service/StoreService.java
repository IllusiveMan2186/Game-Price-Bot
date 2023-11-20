package com.gpb.web.service;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;

import java.util.List;

/**
 * Class for handling specific store
 */
public interface StoreService {

    /**
     * Find game that not created from specific store by url
     *
     * @param url games url
     * @return founded game
     */
    Game findUncreatedGameByUrl(String url);

    /**
     * Find game that info for specific store that was has found in other store already by url
     *
     * @param url games url
     * @return founded game for specific store
     */
    GameInShop findByUrl(String url);

    /**
     * Find game that not created from specific store by name
     *
     * @param name games name
     * @return founded game
     */
    List<Game> findUncreatedGameByName(String name);

    /**
     * Find game that info for specific store that was has found in other store already by name
     *
     * @param name games name
     * @return founded game for specific store
     */
    GameInShop findByName(String name);

    /**
     * Add to wishlist in store
     *
     * @param gameInShop game
     */
    void subscribeToGame(GameInShop gameInShop);

    /**
     * Remove from wishlist in store
     *
     * @param gameInShop game
     */
    void unsubscribeFromGame(GameInShop gameInShop);

    /**
     * Check games from wishlist for changing
     *
     * @param gameInShops games that need to be checked
     * @return games that changed
     */
    List<GameInShop>  checkGameInStoreForChange(List<GameInShop> gameInShops);
}
