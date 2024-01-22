package com.gpb.web.service;

import com.gpb.web.bean.user.UserActivation;
import com.gpb.web.bean.user.WebUser;

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
