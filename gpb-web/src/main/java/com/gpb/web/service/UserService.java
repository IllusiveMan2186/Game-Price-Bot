package com.gpb.web.service;

import com.gpb.web.bean.user.Credentials;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.UserRegistration;

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
    UserDto createUser(UserRegistration user);

    /**
     * Update registered user
     *
     * @param newUser new version of user
     * @param userId user id
     * @return updated user
     */
    UserDto updateUser(UserRegistration newUser, long userId);

    /**
     * Add game to user list of games
     *
     * @param userId users id
     * @param gameId games id
     */
    void subscribeToGame(long userId, long gameId);

    /**
     * Check user credential for authentication
     *
     * @param credentials user authentication credential
     * @return founded user
     */
    UserDto login(Credentials credentials);
}
