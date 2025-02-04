package com.gpb.game.service;

import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;

import java.util.List;

/**
 * Service interface for handling operations related to game stores.
 * <p>
 * This interface provides methods to search for games across integrated game stores and to verify
 * updates in game information for games in users' wishlists.
 * </p>
 */
public interface GameStoresService {

    /**
     * Searches for games by name across all available game stores.
     *
     * @param name the name (or partial name) of the game to search for.
     * @return a list of {@link Game} entities matching the specified name.
     */
    List<Game> findGameByName(String name);

    /**
     * Searches for a game by its URL across all available game stores.
     *
     * @param url the URL associated with the game.
     * @return the {@link Game} entity corresponding to the specified URL, or {@code null} if no match is found.
     */
    Game findGameByUrl(String url);

    /**
     * Checks for updates in the provided list of games from the store.
     * <p>
     * This method compares the current game information with the latest details from the store and returns the list
     * of games that have changed.
     * </p>
     *
     * @param gameInShops a list of {@link GameInShop} entities to be checked for changes.
     * @return a list of {@link GameInShop} entities that have experienced changes in their store information.
     */
    List<GameInShop> checkGameInStoreForChange(List<GameInShop> gameInShops);
}
