package com.gpb.web.service;

import com.gpb.web.bean.WebUser;

public interface UserService {

    WebUser getUserById(long userId);

    WebUser getUserByEmail(String email);

    WebUser createUser(WebUser user);
}
