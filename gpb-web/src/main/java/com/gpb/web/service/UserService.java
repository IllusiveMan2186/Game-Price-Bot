package com.gpb.web.service;

import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.Credentials;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.UserRegistration;

import java.util.List;

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
    UserDto getUserById(long userId);

    /**
     * Get user by email
     *
     * @param email users email
     * @return user
     */
    UserDto getUserByEmail(String email);

    /**
     * Create new user
     *
     * @param user user that would be registered in system
     * @return created user
     */
    WebUser createUser(UserRegistration user);

    /**
     * Update registered user email
     *
     * @param newEmail new version of email
     * @param user     current user
     * @return updated user
     */
    UserDto updateUserEmail(String newEmail, UserDto user);

    /**
     * Update registered user password
     *
     * @param password new version of password
     * @param user     current user
     * @return updated user
     */
    UserDto updateUserPassword(char[] password, UserDto user);

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

    /**
     * Check user credential for authentication
     *
     * @param credentials user authentication credential
     * @return founded user
     */
    UserDto login(Credentials credentials);

    /**
     * @param changedGames changed games
     * @return users that subscribe to changed game
     */
    List<WebUser> getUsersOfChangedGameInfo(List<GameInShop> changedGames);

    /**
     * Change user locale
     *
     * @param locale new locale
     * @param userId users id
     */
    void updateLocale(String locale, long userId);

    /**
     * Activate user wby id
     *
     * @param userId user id
     */
    void activateUser(Long userId);
}
