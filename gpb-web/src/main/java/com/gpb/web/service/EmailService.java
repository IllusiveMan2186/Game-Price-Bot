package com.gpb.web.service;

import com.gpb.web.bean.event.EmailNotificationEvent;
import com.gpb.web.bean.user.UserActivation;
import com.gpb.web.bean.user.WebUser;


public interface EmailService {

    void sendGameInfoChange(WebUser user, EmailNotificationEvent emailNotificationEvent);

    void sendEmailVerification(UserActivation userActivation);
}
