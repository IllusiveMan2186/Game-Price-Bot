package com.gpb.backend.service;

import com.gpb.backend.bean.event.EmailNotificationEvent;
import com.gpb.backend.bean.user.UserActivation;
import com.gpb.backend.bean.user.WebUser;


public interface EmailService {

    void sendGameInfoChange(WebUser user, EmailNotificationEvent emailNotificationEvent);

    void sendEmailVerification(UserActivation userActivation);
}
