package com.gpb.common.service;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for handling basic game operations.
 * <p>
 * This service provides methods for retrieving game details by ID, searching for games by name or genre,
 * fetching games associated with a specific user, and managing the follow status of games for users.
 * </p>
 */
public interface BasicGameService {

    /**
     * Retrieves detailed information for the game identified by the given ID and associates it with the specified user.
     *
     * @param gameId      the unique identifier of the game.
     * @param basicUserId the unique identifier of the user making the request.
     * @return a {@link GameInfoDto} containing detailed information about the game, including user-specific data.
     */
    GameInfoDto getById(long gameId, long basicUserId);

    /**
     * Searches for games by their name with pagination and sorting options.
     *
     * @param name     the name or partial name of the game to search for.
     * @param pageSize the number of results per page.
     * @param pageNum  the page number to retrieve.
     * @param sort     the sort parameters, for example "name-ASC" or "price-DESC".
     * @return a {@link GameListPageDto} containing a paginated list of games that match the search criteria.
     */
    GameListPageDto getByName(String name, int pageSize, int pageNum, String sort, long basicUserId);

    /**
     * Retrieves a paginated list of games filtered by genres, product types to exclude, and a price range.
     *
     * @param genre          a list of genres to include in the search.
     * @param typesToExclude a list of product types to exclude from the search.
     * @param pageSize       the number of results per page.
     * @param pageNum        the page number to retrieve.
     * @param minPrice       the minimum price filter.
     * @param maxPrice       the maximum price filter.
     * @param sort           the sort parameters.
     * @param basicUserId    (optional) the unique identifier of the user.
     * @return a {@link GameListPageDto} containing a paginated list of games that match the specified criteria.
     */
    GameListPageDto getByGenre(List<Genre> genre, List<ProductType> typesToExclude, int pageSize, int pageNum,
                               BigDecimal minPrice, BigDecimal maxPrice, String sort, long basicUserId);

    /**
     * Retrieves a paginated list of games associated with a specific user.
     *
     * @param basicUserId the unique identifier of the user.
     * @param pageSize    the number of results per page.
     * @param pageNum     the page number to retrieve.
     * @param sort        the sort parameters.
     * @return a {@link GameListPageDto} containing a paginated list of games owned or followed by the user.
     */
    GameListPageDto getUserGames(long basicUserId, int pageSize, int pageNum, String sort);

    /**
     * Sets the follow status for a specific game for the given user.
     *
     * @param gameId      the unique identifier of the game.
     * @param basicUserId the unique identifier of the user.
     * @param isFollow    {@code true} to mark the game as followed (to receive updates), or {@code false} to unfollow.
     */
    void setFollowGameOption(long gameId, long basicUserId, boolean isFollow);
}
