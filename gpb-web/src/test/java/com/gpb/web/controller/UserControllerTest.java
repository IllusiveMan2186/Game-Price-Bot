package com.gpb.web.controller;

import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.Test;

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
        WebUser newUser = new WebUser("email2", "password2", false, 0, null);
        newUser.setId(1);
        UserRegistration newUserRegistration = new UserRegistration("email2", "password2".toCharArray());
        UserDto expected = new UserDto(newUser);

        when(service.updateUser(newUserRegistration, 1)).thenReturn(expected);

        UserDto result = controller.updateUser(newUserRegistration, expected);

        assertEquals(expected, result);
    }

    @Test
    void addGameToUserListOfGamesShouldCallServiceAndReturnUser() {
        WebUser user = new WebUser("email", "password", false, 0, null);
        user.setId(1);
        UserDto expected = new UserDto(user);
        when(service.getUserById(1)).thenReturn(expected);

        UserDto result = controller.addGameToUserListOfGames(1, expected);

        assertEquals(expected, result);
        verify(service).subscribeToGame(1, 1);
    }
}