package com.gpb.web.service;

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
     * Get web user by email
     *
     * @param email users email
     * @return user
     */
    WebUser getWebUserByEmail(String email);

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
     * Check user credential for authentication
     *
     * @param credentials user authentication credential
     * @return founded user
     */
    UserDto login(Credentials credentials);

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

    /**
     * Connect telegram user to current web user
     *
     * @param token     token that connected to telegram user
     * @param webUserId web user id
     */
    void connectTelegramUser(String token, long webUserId);

    /**
     * Get token for connect with telegram user
     *
     * @param webUserId web user id
     * @return token of connector
     */
    String getTelegramUserConnectorToken(long webUserId);

    List<WebUser> getWebUsers(List<Long> ids);
}
