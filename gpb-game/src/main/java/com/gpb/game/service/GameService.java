package com.gpb.game.service;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for handling operations related to game entities.
 * <p>
 * This interface defines methods for retrieving game details, searching for games by various criteria,
 * updating game information, managing user subscriptions, and removing games.
 * </p>
 */
public interface GameService {

    /**
     * Retrieves the game with the specified identifier.
     *
     * @param gameId the unique identifier of the game.
     * @return the {@link Game} entity corresponding to the given identifier.
     */
    Game getById(long gameId);

    /**
     * Retrieves the game with the specified identifier with loaded users.
     *
     * @param gameId the unique identifier of the game.
     * @return the {@link Game} entity with loaded users corresponding to the given identifier.
     */
    Game getByIdWithLoadedUsers(long gameId);

    /**
     * Retrieves a detailed Data Transfer Object (DTO) for the game with the specified identifier.
     *
     * @param gameId the unique identifier of the game.
     * @return a {@link GameInfoDto} containing detailed information about the game.
     */
    GameInfoDto getDtoById(long gameId);

    /**
     * Retrieves a paginated list of games that match the specified name.
     *
     * @param name     the name (or partial name) of the game to search for.
     * @param pageSize the number of games to include per page.
     * @param pageNum  the page number to retrieve.
     * @param sort     the sorting criteria to apply.
     * @return a {@link GameListPageDto} containing a list of games that match the specified name.
     */
    GameListPageDto getByName(String name, int pageSize, int pageNum, Sort sort);

    /**
     * Retrieves a paginated list of games filtered by genre, product types to exclude, and price range.
     *
     * @param genres         a list of genres to include in the search.
     * @param typesToExclude a list of product types to exclude from the search.
     * @param pageSize       the number of games to include per page.
     * @param pageNum        the page number to retrieve.
     * @param minPrice       the minimum price filter.
     * @param maxPrice       the maximum price filter.
     * @param sort           the sorting criteria to apply.
     * @return a {@link GameListPageDto} containing the filtered list of games.
     */
    GameListPageDto getByGenre(List<Genre> genres,
                               List<ProductType> typesToExclude,
                               int pageSize,
                               int pageNum,
                               BigDecimal minPrice,
                               BigDecimal maxPrice,
                               Sort sort);

    /**
     * Retrieves a paginated list of games to which a specific user is subscribed.
     *
     * @param userId   the unique identifier of the user.
     * @param pageSize the number of games to include per page.
     * @param pageNum  the page number to retrieve.
     * @param sort     the sorting criteria to apply.
     * @return a {@link GameListPageDto} containing the games associated with the user.
     */
    GameListPageDto getUserGames(long userId, int pageSize, int pageNum, Sort sort);

    /**
     * Removes the game with the specified identifier from the system.
     *
     * @param gameId the unique identifier of the game to be removed.
     */
    void removeGame(long gameId);

    /**
     * Sets the follow status for the specified game.
     *
     * @param gameId   the unique identifier of the game.
     * @param isFollow {@code true} to mark the game as followed, {@code false} otherwise.
     * @return the updated {@link Game} entity reflecting the new follow status.
     */
    Game setFollowGameOption(long gameId, boolean isFollow);

    /**
     * Remove game in shop from game if it last one remove game itself
     *
     * @param gameInShop game in specific store
     */
    void removeGameInShopFromGame(GameInShop gameInShop);
}
