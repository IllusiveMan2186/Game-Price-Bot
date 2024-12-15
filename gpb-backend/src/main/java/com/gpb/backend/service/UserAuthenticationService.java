package com.gpb.backend.service;

import com.gpb.backend.bean.user.Credentials;
import com.gpb.backend.bean.user.UserDto;
import com.gpb.backend.bean.user.UserRegistration;
import com.gpb.backend.bean.user.WebUser;

/**
 * Handle user authentication operations
 */
public interface UserAuthenticationService {
    /**
     * Authenticate user using credentials
     *
     * @param credentials User authentication credentials
     * @return Authenticated user DTO
     */
    UserDto login(Credentials credentials);

    /**
     * Update the user's password
     *
     * @param password New password
     * @param user     Current user
     * @return Updated user DTO
     */
    UserDto updateUserPassword(char[] password, UserDto user);

    /**
     * Update the user's email address
     *
     * @param newEmail New email address
     * @param user     Current user
     * @return Updated user DTO
     */
    UserDto updateUserEmail(String newEmail, UserDto user);

    /**
     * Register a new user
     *
     * @param userRegistration User registration details
     * @return Created web user
     */
    WebUser createUser(UserRegistration userRegistration);

    /**
     * Get user by email
     *
     * @param email users email
     * @return user
     */
    UserDto getUserByEmail(String email);
}

