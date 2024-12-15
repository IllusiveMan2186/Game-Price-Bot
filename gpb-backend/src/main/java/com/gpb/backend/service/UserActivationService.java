package com.gpb.backend.service;

import com.gpb.backend.bean.user.UserActivation;
import com.gpb.backend.bean.user.WebUser;

/**
 * Handle UserActivation entity operations
 */
public interface UserActivationService {

    UserActivation createUserActivation(WebUser user);

    /**
     * Resend the activation email to the user
     *
     * @param email user email
     */
    void resendActivationEmail(String email);

    void activateUserAccount(String token);
}
