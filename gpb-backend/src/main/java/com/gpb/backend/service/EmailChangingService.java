package com.gpb.backend.service;

import com.gpb.backend.entity.EmailChanging;
import com.gpb.backend.entity.WebUser;

/**
 * Service interface for handling email operations.
 */
public interface EmailChangingService {

    /**
     * Creates a new email changing request for the given user.
     *
     * @param newEmail The new email address to be associated with the user.
     * @param user The WebUser requesting the email change.
     * @return An instance of EmailChanging representing the change request.
     */
    EmailChanging createEmailChanging(String newEmail, WebUser user);

    /**
     * Confirms the email change request using the provided token from new and old email.
     *
     * @param confirmationToken The token to verify the email change.
     * @return A message indicating the success or confirmation process.
     */
    String confirmEmailChangingToken(String confirmationToken);
}