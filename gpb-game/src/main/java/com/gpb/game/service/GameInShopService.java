package com.gpb.game.service;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;

import java.util.List;

/**
 * Service interface for handling operations related to game in shop entities.
 * <p>
 * This interface defines methods for retrieving games in shop details, and removing.
 * </p>
 */
public interface GameInShopService {

    /**
     * Retrieves detailed information for a game based on its URL.
     *
     * @param url the URL associated with the game in the store.
     * @return a {@link GameInfoDto} containing the game's detailed information.
     */
    GameInfoDto getByUrl(String url);

    /**
     * Retrieves all games available in the shop.
     *
     * @return a list of all {@link GameInShop} entities present in the shop.
     */
    List<GameInShop> getAllGamesInShop();


    /**
     * Retrieves the list of games for which all users are subscribed.
     *
     * @return a list of {@link GameInShop} entities that users are subscribed to.
     */
    List<GameInShop> getSubscribedGames();

    /**
     * Updates the game information based on the provided list of changed games.
     *
     * @param changedGames a list of {@link GameInShop} entities whose information has been updated.
     */
    void changeInfo(List<GameInShop> changedGames);

    /**
     * Retrieves the list of changed games that a specific user is subscribed to.
     *
     * @param user         the {@link BasicUser} whose subscriptions are to be checked.
     * @param changedGames a list of all changed {@link GameInShop} entities.
     * @return a list of {@link GameInShop} entities that are both changed and subscribed to by the user.
     */
    List<GameInShop> getUsersChangedGames(BasicUser user, List<GameInShop> changedGames);

    /**
     * Add game from store to created game by url and return game with new info
     *
     * @param url    url to game in store
     * @param gameId the unique identifier of the game to which game in store would be added
     * @return a {@link GameInfoDto} containing detailed information about the game with added game in store
     */
    GameInfoDto addGameInStore(long gameId, String url);

    /**
     * Removes the game from the store with the specified identifier.
     *
     * @param gameInStoreId the unique identifier of the game in the store to be removed.
     */
    void removeGameInStore(long gameInStoreId);
}
