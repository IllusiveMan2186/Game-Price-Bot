package com.gpb.backend.service;

import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.UserDto;

import java.util.List;

/**
 * Handle user info management
 */
public interface UserManagementService {
    /**
     * Get user by ID
     *
     * @param userId User's ID
     * @return User DTO
     */
    UserDto getUserById(long userId);

    /**
     * Get user by basic user ID
     *
     * @param basicUserId Basic user ID
     * @return Web user
     */
    WebUser getUserByBasicUserId(long basicUserId);

    /**
     * Get web users by a list of IDs
     *
     * @param ids List of user IDs
     * @return List of web users
     */
    List<WebUser> getWebUsers(List<Long> ids);

    /**
     * Get web user by email
     *
     * @param email users email
     * @return user
     */
    WebUser getWebUserByEmail(String email);

    /**
     * Update the user's locale
     *
     * @param locale New locale
     * @param userId User's ID
     */
    void updateLocale(String locale, long userId);

    /**
     * Activate user by ID
     *
     * @param userId User's ID
     */
    void activateUser(long userId);

    /**
     * Set new basic user id
     *
     * @param currentBasicUserId current basic user id
     * @param newBasicUserId     new basic user id
     */
    void setBasicUserId(long currentBasicUserId, long newBasicUserId);
}

