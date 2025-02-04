package com.gpb.telegram.service;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;

/**
 * Service interface for managing game-related operations in the Telegram context.
 * <p>
 * This interface provides methods for retrieving game details by various criteria,
 * managing user-specific game lists, and setting game follow options.
 * </p>
 */
public interface GameService {

    /**
     * Retrieves detailed information for a game by its ID.
     *
     * @param gameId the unique identifier of the game
     * @param basicUserId the unique identifier of the user requesting the game information;
     *               this can be used to tailor the response based on the user's preferences.
     * @return a {@link GameInfoDto} containing detailed information about the game
     */
    GameInfoDto getById(long gameId, long basicUserId);

    /**
     * Retrieves a paginated list of games matching the specified name.
     *
     * @param name        the name or partial name of the game to search for
     * @param pageNum     the page number to retrieve (starting from 1)
     * @param basicUserId the basic user id
     * @return a {@link GameListPageDto} containing a page of games that match the name,
     * along with additional paging information
     */
    GameListPageDto getByName(String name, int pageNum, long basicUserId);

    /**
     * Retrieves a paginated list of games sorted according to the specified parameters.
     *
     * @param pageNum the page number to retrieve (starting from 1)
     * @param sort    a string representing the sort criteria (e.g., "discountPrice-ASC")
     * @param basicUserId the basic user id
     * @return a {@link GameListPageDto} containing a page of games sorted as requested,
     * along with the total number of matched records
     */
    GameListPageDto getGameList(int pageNum, String sort, long basicUserId);

    /**
     * Sets the follow option for a specific game for a user.
     * <p>
     * This determines whether the user is subscribed to updates or notifications for the game.
     * </p>
     *
     * @param gameId   the unique identifier of the game
     * @param basicUserId   the unique identifier of the user
     * @param isFollow {@code true} if the user should follow the game; {@code false} otherwise
     */
    void setFollowGameOption(long gameId, long basicUserId, boolean isFollow);

    /**
     * Retrieves a paginated list of games associated with the specified user.
     *
     * @param basicUserId the unique identifier of the user (basic user ID)
     * @param pageNum     the page number to retrieve (starting from 1)
     * @return a {@link GameListPageDto} containing a page of the user's games along with paging details
     */
    GameListPageDto getUserGames(long basicUserId, int pageNum);
}