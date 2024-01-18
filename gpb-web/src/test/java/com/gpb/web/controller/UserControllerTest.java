package com.gpb.web.controller;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.user.EmailChangeDto;
import com.gpb.web.bean.user.PasswordChangeDto;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.configuration.MapperConfig;
import com.gpb.web.configuration.UserAuthenticationProvider;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static com.gpb.web.util.Constants.USER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    UserService service = mock(UserService.class);

    GameService gameService = mock(GameService.class);

    GameStoresService gameStoresService = mock(GameStoresService.class);

    UserAuthenticationProvider provider = mock(UserAuthenticationProvider.class);

    private final UserController controller = new UserController(service, gameStoresService, gameService, provider);
    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    private final WebUser user = new WebUser("email", "password", false, false, 0, null, USER_ROLE);

    @Test
    void updateUserEmailSuccessfullyShouldReturnUser() {
        WebUser newUser = new WebUser("email2", "password2", false, false, 0, null, USER_ROLE);
        newUser.setId(1);
        UserDto expected = modelMapper.map(newUser, UserDto.class);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        EmailChangeDto emailChangeDto = new EmailChangeDto();
        emailChangeDto.setEmail(newUser.getEmail());
        when(service.updateUserEmail(newUser.getEmail(), userDto)).thenReturn(expected);

        UserDto result = controller.updateUserEmail(emailChangeDto, userDto);

        assertEquals(expected, result);
    }

    @Test
    void updateUserPasswordSuccessfullyShouldReturnUser() {
        WebUser newUser = new WebUser("email2", "password2", false, false, 0, null, USER_ROLE);
        newUser.setId(1);
        UserDto expected = modelMapper.map(newUser, UserDto.class);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setPassword(newUser.getPassword().toCharArray());
        when(service.updateUserPassword(newUser.getPassword().toCharArray(), userDto)).thenReturn(expected);

        UserDto result = controller.updateUserPassword(passwordChangeDto, userDto);

        assertEquals(expected, result);
    }

    @Test
    void addGameToUserListOfGamesShouldCallServiceAndReturnUser() {
        WebUser user = new WebUser("email", "password", false, false, 0, null, USER_ROLE);
        user.setId(1);
        UserDto expected = modelMapper.map(user, UserDto.class);
        when(service.getUserById(1)).thenReturn(expected);
        Game game = new Game();
        game.setUserList(List.of(new WebUser()));
        when(gameService.getById(1)).thenReturn(game);

        UserDto result = controller.addGameToUserListOfGames(1, expected);

        assertEquals(expected, result);
        verify(gameStoresService).subscribeToGame(game);
        verify(service).subscribeToGame(1, 1);
    }

    @Test
    void removeGameFromUserListOfGamesShouldCallServiceAndReturnUser() {
        WebUser user = new WebUser("email", "password", false, false, 0, null, USER_ROLE);
        user.setId(1);
        UserDto expected = modelMapper.map(user, UserDto.class);
        when(service.getUserById(1)).thenReturn(expected);
        Game game = new Game();
        game.setUserList(new ArrayList<>());
        when(gameService.getById(1)).thenReturn(game);

        UserDto result = controller.removeGameFromUserListOfGames(1, expected);

        assertEquals(expected, result);
        verify(gameStoresService).unsubscribeFromGame(game);
        verify(service).unsubscribeFromGame(1, 1);
    }
}