package com.gpb.game.service;

import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;

import java.util.List;

/**
 * Class for handling users
 */
public interface UserService {

    BasicUser getUserById(long userId);

    /**
     * Create new basic user
     *
     * @param notificationType notification type for user
     * @return basic user
     */
    BasicUser createUser(UserNotificationType notificationType);

    /**
     * Merge info from one user to other
     */
    BasicUser linkUsers(String token, long sourceUserId);

    /**
     * Get token for link accounts with user
     *
     * @param userId basic user id
     * @return accounts link token
     */
    String createAccountLinkerToken(long userId);

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
