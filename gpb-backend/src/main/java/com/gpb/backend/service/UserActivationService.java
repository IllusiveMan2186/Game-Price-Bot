package com.gpb.backend.service;

import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.WebUser;

/**
 * Service interface for managing user activation operations.
 */
public interface UserActivationService {

    /**
     * Creates a new {@link UserActivation} instance for the specified user.
     *
     * @param user the {@link WebUser} for whom the activation is to be created
     * @return the newly created {@link UserActivation} instance
     */
    UserActivation createUserActivation(WebUser user);

    /**
     * Resends the activation email to the specified email address.
     *
     * @param email the email address of the user to whom the activation email should be sent
     */
    void resendActivationEmail(String email);

    /**
     * Activates the user account associated with the provided activation token.
     *
     * @param token the activation token used to verify and activate the user account
     */
    void activateUserAccount(String token);
}