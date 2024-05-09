package com.gpb.telegram.service;

import com.gpb.telegram.bean.Game;

import java.util.List;

public interface GameService {

    /**
     * Get game by id
     *
     * @param gameId games id
     * @return game
     */
    Game getById(long gameId);

    /**
     * Get game by name
     *
     * @param name    games name
     * @param pageNum page number
     * @return game
     */
    List<Game> getByName(final String name, final int pageNum);

    /**
     * Get amount of games by name
     *
     * @param name games name
     * @return amount of games
     */
    long getGameAmountByName(final String name);

    /**
     * Check if user subscribed to the game
     *
     * @param gameId game id
     * @param userId user id
     * @return true if user subscribed to the game
     */
    boolean isSubscribed(long gameId,long userId);
}
