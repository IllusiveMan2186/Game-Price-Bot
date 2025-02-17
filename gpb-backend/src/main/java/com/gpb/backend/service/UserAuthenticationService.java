package com.gpb.backend.service;

import com.gpb.backend.entity.Credentials;
import com.gpb.backend.entity.UserRegistration;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.UserDto;

/**
 * Service interface for handling user authentication operations.
 */
public interface UserAuthenticationService {

    /**
     * Authenticates a user using the provided credentials.
     *
     * @param credentials the {@link Credentials} containing the user's authentication information
     * @return an authenticated {@link WebUser} representing the user
     */
    WebUser login(Credentials credentials);

    /**
     * Updates the password for the specified user.
     *
     * @param oldPassword the old password
     * @param newPassword the new password
     * @param user        the current authenticated {@link UserDto} whose password is to be updated
     * @return an updated {@link UserDto} after the password change
     */
    UserDto updateUserPassword(char[] oldPassword, char[] newPassword, UserDto user);

    /**
     * Updates the email address for the specified user.
     *
     * @param newEmail the new email address
     * @param user     the current authenticated {@link UserDto} whose email is to be updated
     * @return an updated {@link UserDto} after the email change
     */
    UserDto updateUserEmail(String newEmail, WebUser user);

    /**
     * Registers a new user using the provided registration details.
     *
     * @param userRegistration the {@link UserRegistration} details for the new user
     * @return a newly created {@link WebUser} representing the registered user
     */
    WebUser createUser(UserRegistration userRegistration);

    /**
     * Retrieves the user information for a given user ID.
     *
     * @param userId the unique identifier of the user
     * @return a {@link UserDto} representing the user's details
     */
    UserDto getUserById(long userId);
}