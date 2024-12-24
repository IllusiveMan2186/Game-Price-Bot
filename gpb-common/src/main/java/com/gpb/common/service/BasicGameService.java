package com.gpb.common.service;

import com.gpb.common.entity.game.GameInfoDto;

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
}
