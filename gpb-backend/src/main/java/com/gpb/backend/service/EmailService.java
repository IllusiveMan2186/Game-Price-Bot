package com.gpb.backend.service;

import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.WebUser;
import com.gpb.common.entity.event.EmailNotificationEvent;


public interface EmailService {

    void sendGameInfoChange(WebUser user, EmailNotificationEvent emailNotificationEvent);

    void sendEmailVerification(UserActivation userActivation);
}
