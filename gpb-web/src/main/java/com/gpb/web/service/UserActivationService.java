package com.gpb.web.service;

import com.gpb.web.bean.user.UserActivation;
import com.gpb.web.bean.user.WebUser;

public interface UserActivationService {

    UserActivation createUserActivation(WebUser user);

    void activateUserAccount(String token);
}
