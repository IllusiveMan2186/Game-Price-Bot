package com.gpb.backend.service;

import com.gpb.common.entity.game.AddGameInStoreDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for managing game-related operations.
 */
public interface GameService {

    /**
     * Retrieves detailed information for a game by its ID.
     *
     * @param gameId the unique identifier of the game
     * @param userId the unique identifier of the user requesting the game information;
     *               this parameter may be used to tailor the response based on the user's preferences.
     * @return a {@link GameInfoDto} containing detailed information about the game
     */
    GameInfoDto getById(long gameId, long userId);

    /**
     * Retrieves a paginated list of games that match the specified name.
     *
     * @param name     the name or partial name of the game to search for
     * @param pageSize the number of game records per page
     * @param pageNum  the page number to retrieve (starting from 1)
     * @param sort     a string representing the sort criteria (e.g., "gamesInShop.discountPrice-ASC")
     * @return a {@link GameListPageDto} containing a list of games and the total count of matched records
     */
    GameListPageDto getByName(String name, int pageSize, int pageNum, String sort);

    /**
     * Retrieves detailed information for a game using its store URL.
     *
     * @param url the store URL of the game
     * @return a {@link GameInfoDto} containing detailed information about the game
     */
    GameInfoDto getByUrl(String url);

    /**
     * Add game from store to created game by url
     *
     * @param addGameInStoreDto the dto containing the game ID to which game in store
     *                          should be added and game in store url.
     */
    void addGameInStore(AddGameInStoreDto addGameInStoreDto);

    /**
     * Retrieves a paginated list of games filtered by genres, excluding specified product types,
     * and within a specified price range.
     *
     * @param genre          a list of {@link Genre} values to filter games by (optional)
     * @param typesToExclude a list of {@link ProductType} values to exclude from the search (optional)
     * @param pageSize       the number of game records per page
     * @param pageNum        the page number to retrieve (starting from 1)
     * @param minPrice       the minimum price of the games to include
     * @param maxPrice       the maximum price of the games to include
     * @param sort           a string representing the sort criteria (e.g., "gamesInShop.discountPrice-ASC")
     * @return a {@link GameListPageDto} containing a list of games matching the specified criteria and the total count
     */
    GameListPageDto getByGenre(List<Genre> genre,
                               List<ProductType> typesToExclude,
                               int pageSize,
                               int pageNum,
                               BigDecimal minPrice,
                               BigDecimal maxPrice,
                               String sort);

    /**
     * Retrieves a paginated list of games associated with a specific user.
     *
     * @param userId   the unique identifier of the user whose games are to be retrieved
     * @param pageSize the number of game records per page
     * @param pageNum  the page number to retrieve (starting from 1)
     * @param sort     a string representing the sort criteria (e.g., "gamesInShop.discountPrice-ASC")
     * @return a {@link GameListPageDto} containing a list of games for the user and the total count of the user's games
     */
    GameListPageDto getUserGames(long userId, int pageSize, int pageNum, String sort);

    /**
     * Removes a game from the system by its unique identifier.
     *
     * @param gameId the unique identifier of the game to be removed
     */
    void removeGame(long gameId);

    /**
     * Removes a game in store from by its store-specific identifier.
     *
     * @param gameInStoreId the unique identifier of the game in the store to be removed
     */
    void removeGameInStore(long gameInStoreId);

    /**
     * Sets the follow option for a game for a specific user. This option determines whether
     * the user is subscribed to notifications or updates about the game.
     *
     * @param gameId   the unique identifier of the game
     * @param userId   the unique identifier of the user
     * @param isFollow {@code true} if the user should follow the game; {@code false} otherwise
     */
    void setFollowGameOption(long gameId, long userId, boolean isFollow);
}