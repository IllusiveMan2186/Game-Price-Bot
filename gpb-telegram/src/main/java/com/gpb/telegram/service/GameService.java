package com.gpb.telegram.service;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;

public interface GameService {

    /**
     * Get game by id
     *
     * @param gameId games id
     * @param userId user id
     * @return game
     */
    GameInfoDto getById(long gameId, long userId);

    /**
     * Get game by name
     *
     * @param name    games name
     * @param pageNum page number
     * @return page with all games with needed name
     */
    GameListPageDto getByName(final String name, final int pageNum);

    /**
     * Set isFollowed field in game for user by game and user id
     *
     * @param gameId   games id
     * @param userId   user id
     * @param isFollow is program would follow this game for information changing
     */
    void setFollowGameOption(long gameId, long userId, boolean isFollow);
}
