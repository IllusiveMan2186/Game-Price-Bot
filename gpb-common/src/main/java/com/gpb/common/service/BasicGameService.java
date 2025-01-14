package com.gpb.common.service;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;

public interface BasicGameService {

    /**
     * Get game by id
     *
     * @param gameId games id
     * @param userId user id
     * @return game
     */
    GameInfoDto getById(long gameId, long userId);

    /**
     * Set isFollowed field in game for user by game and user id
     *
     * @param gameId   games id
     * @param userId   user id
     * @param isFollow is program would follow this game for information changing
     */
    void setFollowGameOption(long gameId, long userId, boolean isFollow);

    /**
     * Get games of user
     *
     * @param userId   user id
     * @param pageSize amount of elements on page
     * @param pageNum  page number
     * @param sort     sort parameters
     * @return list of user`s games with all amount
     */
    GameListPageDto getUserGames(long userId, int pageSize, int pageNum, String sort);
}
