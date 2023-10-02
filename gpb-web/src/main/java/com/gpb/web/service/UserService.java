package com.gpb.web.service;

import com.gpb.web.bean.user.WebUser;

/**
 * Class for handling users
 */
public interface UserService {

    /**
     * Get user by id
     *
     * @param userId users id
     * @return user
     */
    WebUser getUserById(long userId);

    /**
     * Get user by email
     *
     * @param email users email
     * @return user
     */
    WebUser getUserByEmail(String email);

    /**
     * Create new user
     *
     * @param user user that would be registered in system
     * @return created user
     */
    WebUser createUser(WebUser user);

    /**
     * Update registered user
     *
     * @param newUser new version of user
     * @param oldUser old version of user
     * @return updated user
     */
    WebUser updateUser(WebUser newUser, WebUser oldUser);

    /**
     * Add game to user list of games
     *
     * @param userId users id
     * @param gameId games id
     */
    void addGameToUserListOfGames(long userId, long gameId);
}
