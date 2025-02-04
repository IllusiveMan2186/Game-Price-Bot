package com.gpb.backend.service;

import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.UserDto;

import java.util.List;

/**
 * Service interface for managing user information.
 */
public interface UserManagementService {

    /**
     * Retrieves the user information for a given user ID.
     *
     * @param userId the unique identifier of the user
     * @return a {@link UserDto} representing the user's details
     */
    UserDto getUserById(long userId);

    /**
     * Retrieves the web user associated with the given basic user ID.
     *
     * @param basicUserId the basic user ID
     * @return the corresponding {@link WebUser} entity
     */
    WebUser getUserByBasicUserId(long basicUserId);

    /**
     * Retrieves a web user by their email address.
     *
     * @param email the email address of the user
     * @return the {@link WebUser} associated with the specified email
     */
    WebUser getWebUserByEmail(String email);

    /**
     * Updates the locale setting for the specified user.
     *
     * @param locale the new locale to be set for the user
     * @param userId the unique identifier of the user whose locale is being updated
     */
    void updateLocale(String locale, long userId);

    /**
     * Activates the user account corresponding to the specified user ID.
     *
     * @param userId the unique identifier of the user to activate
     */
    void activateUser(long userId);
}