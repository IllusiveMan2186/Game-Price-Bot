package com.gpb.game.service;

import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling operations related to a specific game store.
 * <p>
 * This interface defines methods for finding games based on their URL or name,
 * both for games that have not yet been created for the store and for retrieving existing store-specific game information.
 * It also provides functionality for checking updates in game information.
 * </p>
 */
public interface StoreService {

    /**
     * Finds a game that has not yet been created for this specific store using its URL.
     *
     * @param url the URL of the game.
     * @return the {@link Game} entity that has not been created for the store, or {@code null} if no matching game is found.
     */
    Optional<Game> findUncreatedGameByUrl(String url);

    /**
     * Finds the store-specific game information for a game using its URL.
     * <p>
     * This method retrieves a {@link GameInShop} entity that represents the game information for this store,
     * which may have been discovered in another store.
     * </p>
     *
     * @param url the URL of the game.
     * @return the {@link GameInShop} entity for the specified URL, or {@code null} if not found.
     */
    Optional<GameInShop> findByUrl(String url);

    /**
     * Finds games that have not yet been created for this specific store by their name.
     *
     * @param name the name (or partial name) of the game.
     * @return a list of {@link Game} entities that have not been created for the store.
     */
    List<Game> findUncreatedGameByName(String name);

    /**
     * Finds the store-specific game information for a game using its name.
     * <p>
     * This method retrieves a {@link GameInShop} entity for the given game name,
     * representing the game information specific to this store.
     * </p>
     *
     * @param name the name of the game.
     * @return the {@link GameInShop} entity corresponding to the specified name, or {@code null} if not found.
     */
    Optional<GameInShop> findByName(String name);

    /**
     * Checks the provided list of games in the store for any changes in their information.
     * <p>
     * This method compares the current game details with the latest data from the store and returns only those games
     * that have experienced changes.
     * </p>
     *
     * @param gameInShops a list of {@link GameInShop} entities to be checked for updates.
     * @return a list of {@link GameInShop} entities that have updated information.
     */
    List<GameInShop> checkGameInStoreForChange(List<GameInShop> gameInShops);
}
