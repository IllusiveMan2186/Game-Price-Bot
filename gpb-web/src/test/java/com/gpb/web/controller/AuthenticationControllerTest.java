package com.gpb.web.controller;

import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.configuration.UserAuthenticationProvider;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {

    UserService service = mock(UserService.class);

    UserAuthenticationProvider provider = mock(UserAuthenticationProvider.class);

    private final AuthenticationController controller = new AuthenticationController(service, provider);

    private final WebUser user = new WebUser("email", "password");

    @Test
    void createUserSuccessfullyShouldReturnUser() {
        UserRegistration userRegistration = new UserRegistration("email", "password".toCharArray());
        UserDto userDto = new UserDto(user);
        when(service.createUser(userRegistration)).thenReturn(userDto);
        UserDto expected = new UserDto(user);

        UserDto result = controller.userRegistration(userRegistration);

        assertEquals(expected, result);
    }
}