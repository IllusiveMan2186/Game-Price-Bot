package com.gpb.web.controller;

import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    UserService service = mock(UserService.class);

    private final UserController controller = new UserController(service);

    private final WebUser user = new WebUser("email", "password", false, 0, null);

    @Test
    void updateUserSuccessfullyShouldReturnUser() {
        HttpSession session = mock(HttpSession.class);
        SecurityContextImpl securityContext = mock(SecurityContextImpl.class);
        when(session.getAttribute("SPRING_SECURITY_CONTEXT")).thenReturn(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        user.setId(1);
        when(authentication.getPrincipal()).thenReturn(new UserDto(user));
        WebUser newUser = new WebUser("email2", "password2", false, 0, null);
        UserRegistration newUserRegistration = new UserRegistration("email2", "password2".toCharArray());
        UserDto expected = new UserDto(newUser);
        when(service.updateUser(newUserRegistration, 1)).thenReturn(expected);

        UserDto result = controller.updateUser(newUserRegistration, session);

        assertEquals(expected, result);
    }

    @Test
    void addGameToUserListOfGamesShouldCallServiceAndReturnUser() {
        user.setId(1);
        HttpSession session = mock(HttpSession.class);
        SecurityContextImpl securityContext = mock(SecurityContextImpl.class);
        when(session.getAttribute("SPRING_SECURITY_CONTEXT")).thenReturn(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        user.setId(1);
        UserDto expected = new UserDto(user);
        when(authentication.getPrincipal()).thenReturn(expected);
        when(service.getUserById(1)).thenReturn(expected);

        UserDto result = controller.addGameToUserListOfGames(1, session);


        assertEquals(expected, result);
        verify(service).addGameToUserListOfGames(1, 1);
    }
}