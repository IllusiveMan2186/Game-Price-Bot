package com.gpb.stores.service;

import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.bean.user.BasicUser;

import java.util.List;

/**
 * Class for handling users
 */
public interface UserService {

    BasicUser createUser();

    /**
     * @param changedGames changed games
     * @return users that subscribe to changed game
     */
    List<BasicUser> getUsersOfChangedGameInfo(List<GameInShop> changedGames);

    /**
     * Add game to user list of games
     *
     * @param userId users id
     * @param gameId games id
     */
    void subscribeToGame(long userId, long gameId);

    /**
     * Add game to user list of games
     *
     * @param userId users id
     * @param gameId games id
     */
    void unsubscribeFromGame(long userId, long gameId);
}
