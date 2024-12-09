package com.gpb.web.unit.controller;

import com.gpb.web.bean.user.PasswordChangeDto;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.configuration.MapperConfig;
import com.gpb.web.configuration.UserAuthenticationProvider;
import com.gpb.web.controller.UserController;
import com.gpb.web.service.UserActivationService;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.gpb.web.util.Constants.USER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    UserService userService = mock(UserService.class);

    GameService gameService = mock(GameService.class);

    GameStoresService gameStoresService = mock(GameStoresService.class);

    UserActivationService userActivationService = mock(UserActivationService.class);

    UserAuthenticationProvider provider = mock(UserAuthenticationProvider.class);

    private final UserController controller = new UserController(userService, gameStoresService, gameService, userActivationService, provider);
    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    private final WebUser user = new WebUser(0, new BasicUser(), "email", "password", false,
            false, 0, null, USER_ROLE, new Locale("ua"));

    @Test
    void updateUserEmailSuccessfullyShouldReturnUser() {
        WebUser newUser = new WebUser(0, new BasicUser(), "email2", "password2", false,
                false, 0, null, USER_ROLE, new Locale("ua"));
        newUser.setId(1);
        UserDto expected = modelMapper.map(newUser, UserDto.class);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        when(userService.updateUserEmail(newUser.getEmail(), userDto)).thenReturn(expected);

        UserDto result = controller.updateUserEmail(newUser.getEmail(), userDto);

        assertEquals(expected, result);
    }

    @Test
    void updateUserPasswordSuccessfullyShouldReturnUser() {
        WebUser newUser = new WebUser(0, new BasicUser(), "email2", "password2", false,
                false, 0, null, USER_ROLE, new Locale("ua"));
        newUser.setId(1);
        UserDto expected = modelMapper.map(newUser, UserDto.class);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setPassword(newUser.getPassword().toCharArray());
        when(userService.updateUserPassword(newUser.getPassword().toCharArray(), userDto)).thenReturn(expected);

        UserDto result = controller.updateUserPassword(passwordChangeDto, userDto);

        assertEquals(expected, result);
    }

    @Test
    void addGameToUserListOfGamesShouldCallServiceAndReturnUser() {
        WebUser user = new WebUser(0, new BasicUser(), "email", "password", false,
                false, 0, null, USER_ROLE, new Locale("ua"));
        user.setId(1);
        UserDto expected = modelMapper.map(user, UserDto.class);
        when(userService.getUserById(1)).thenReturn(expected);
        Game game = new Game();
        game.setUserList(List.of(new BasicUser()));
        when(gameService.getById(1)).thenReturn(game);

        UserDto result = controller.addGameToUserListOfGames(1, expected);

        assertEquals(expected, result);
        verify(gameStoresService).subscribeToGame(1);
        verify(userService).subscribeToGame(1, 1);
    }

    @Test
    void removeGameFromUserListOfGamesShouldCallServiceAndReturnUser() {
        WebUser user = new WebUser(0, new BasicUser(), "email", "password", false,
                false, 0, null, USER_ROLE, new Locale("ua"));
        user.setId(1);
        UserDto expected = modelMapper.map(user, UserDto.class);
        when(userService.getUserById(1)).thenReturn(expected);
        Game game = new Game();
        game.setUserList(new ArrayList<>());
        game.setFollowed(true);
        when(gameService.getById(1)).thenReturn(game);

        UserDto result = controller.removeGameFromUserListOfGames(1, expected);

        assertEquals(expected, result);
        verify(gameStoresService).unsubscribeFromGame(1);
        verify(userService).unsubscribeFromGame(1, 1);
    }

    @Test
    void updateUserLocaleSuccessfullyShouldCallUserServiceMethod() {
        String stringLocale = "locale";
        WebUser user = new WebUser(0, new BasicUser(), "email", "password", false,
                false, 0, null, USER_ROLE, new Locale("ua"));
        user.setId(1);
        UserDto userDto = modelMapper.map(user, UserDto.class);

        controller.updateUserLocale(stringLocale, userDto);

        verify(userService).updateLocale(stringLocale, user.getId());
    }

    @Test
    void resendUserActivationEmailSuccessfullyShouldCallUserActivationServiceMethod() {
        String email = "email";
        controller.resendUserActivationEmail(email);

        verify(userActivationService).resendActivationEmail(email);
    }
}