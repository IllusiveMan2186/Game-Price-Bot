package com.gpb.web.controller;

import com.gpb.web.bean.user.EmailChangeDto;
import com.gpb.web.bean.user.PasswordChangeDto;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.configuration.MapperConfig;
import com.gpb.web.configuration.UserAuthenticationProvider;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    UserService service = mock(UserService.class);

    UserAuthenticationProvider provider = mock(UserAuthenticationProvider.class);

    private final UserController controller = new UserController(service, provider);
    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    private final WebUser user = new WebUser("email", "password", false, 0, null);

    @Test
    void updateUserEmailSuccessfullyShouldReturnUser() {
        WebUser newUser = new WebUser("email2", "password2", false, 0, null);
        newUser.setId(1);
        UserDto expected = modelMapper.map(newUser,UserDto.class);
        UserDto userDto = modelMapper.map(user,UserDto.class);
        EmailChangeDto emailChangeDto = new EmailChangeDto();
        emailChangeDto.setEmail(newUser.getEmail());
        when(service.updateUserEmail(newUser.getEmail(), userDto)).thenReturn(expected);

        UserDto result = controller.updateUserEmail(emailChangeDto, userDto);

        assertEquals(expected, result);
    }

    @Test
    void updateUserPasswordSuccessfullyShouldReturnUser() {
        WebUser newUser = new WebUser("email2", "password2", false, 0, null);
        newUser.setId(1);
        UserDto expected = modelMapper.map(newUser,UserDto.class);
        UserDto userDto = modelMapper.map(user,UserDto.class);
        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setPassword(newUser.getPassword().toCharArray());
        when(service.updateUserPassword(newUser.getPassword().toCharArray(), userDto)).thenReturn(expected);

        UserDto result = controller.updateUserPassword(passwordChangeDto, userDto);

        assertEquals(expected, result);
    }

    @Test
    void addGameToUserListOfGamesShouldCallServiceAndReturnUser() {
        WebUser user = new WebUser("email", "password", false, 0, null);
        user.setId(1);
        UserDto expected = modelMapper.map(user,UserDto.class);
        when(service.getUserById(1)).thenReturn(expected);

        UserDto result = controller.addGameToUserListOfGames(1, expected);

        assertEquals(expected, result);
        verify(service).subscribeToGame(1, 1);
    }
}