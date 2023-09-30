package com.gpb.web.controller;

import com.gpb.web.bean.UserInfo;
import com.gpb.web.bean.WebUser;
import com.gpb.web.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    UserService service = mock(UserService.class);

    private final UserController controller = new UserController(service);

    private final WebUser user = new WebUser("email", "password");

    @Test
    void getUserByIdSuccessfullyShouldReturnUser() {
        int id = 1;
        when(service.getUserById(id)).thenReturn(user);
        UserInfo expected = new UserInfo(user);

        UserInfo result = controller.getUserById(id);


        assertEquals(expected, result);
    }


    @Test
    void createUserSuccessfullyShouldReturnUser() {
        when(service.createUser(user)).thenReturn(user);
        UserInfo expected = new UserInfo(user);

        UserInfo result = controller.userRegistration(user);

        assertEquals(expected, result);
    }

    @Test
    void updateUserSuccessfullyShouldReturnUser() {
        HttpSession session = mock(HttpSession.class);
        SecurityContextImpl securityContext = mock(SecurityContextImpl.class);
        when(session.getAttribute("SPRING_SECURITY_CONTEXT")).thenReturn(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        WebUser newUser = new WebUser("email2", "password2");
        when(service.updateUser(newUser, user)).thenReturn(newUser);
        UserInfo expected = new UserInfo(newUser);

        UserInfo result = controller.updateUser(newUser, session);

        assertEquals(expected, result);
    }
}