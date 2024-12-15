package com.gpb.backend.unit.controller;

import com.gpb.backend.bean.user.PasswordChangeDto;
import com.gpb.backend.bean.user.UserDto;
import com.gpb.backend.configuration.UserAuthenticationProvider;
import com.gpb.backend.controller.UserController;
import com.gpb.backend.service.GameService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.service.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserManagementService userManagementService;
    @Mock
    private UserAuthenticationService userAuthenticationService;
    ;

    @Mock
    private GameService gameService;

    @Mock
    private UserActivationService userActivationService;

    @Mock
    private UserAuthenticationProvider userAuthenticationProvider;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateUserEmail_shouldReturnUpdatedUser() {
        String newEmail = "newemail@example.com";
        UserDto user = new UserDto("username", "password", "tpken", "role", "ua");
        user.setId(123L);

        UserDto updatedUser = new UserDto("username", "password", "tpken", "role", "ua");
        updatedUser.setEmail(newEmail);
        when(userAuthenticationService.updateUserEmail(newEmail, user)).thenReturn(updatedUser);
        when(userAuthenticationProvider.createToken(newEmail)).thenReturn("new-token");


        UserDto result = userController.updateUserEmail(newEmail, user);


        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
        assertEquals("new-token", result.getToken());
        verify(userAuthenticationService).updateUserEmail(newEmail, user);
        verify(userAuthenticationProvider).createToken(newEmail);
    }

    @Test
    void updateUserPassword_shouldReturnUpdatedUser() {
        char[] password = "newPassword123".toCharArray();
        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setPassword(password);
        UserDto user = new UserDto("username", "password", "tpken", "role", "ua");
        user.setId(123L);

        UserDto updatedUser = new UserDto("username", "password", "tpken", "role", "ua");
        when(userAuthenticationService.updateUserPassword(password, user)).thenReturn(updatedUser);


        UserDto result = userController.updateUserPassword(passwordChangeDto, user);


        assertNotNull(result);
        verify(userAuthenticationService).updateUserPassword(password, user);
    }

    @Test
    void resendUserActivationEmail_shouldInvokeServiceMethod() {
        String email = "user@example.com";


        userController.resendUserActivationEmail(email);


        verify(userActivationService).resendActivationEmail(email);
    }

    @Test
    void addGameToUserListOfGames_shouldReturnUpdatedUser() {
        long gameId = 1L;
        UserDto user = new UserDto("username", "password", "tpken", "role", "ua");
        user.setId(123L);


        userController.addGameToUserListOfGames(gameId, user);


        verify(gameService).setFollowGameOption(gameId, user.getId(), true);
    }

    @Test
    void removeGameFromUserListOfGames_shouldReturnUpdatedUser() {
        long gameId = 1L;
        UserDto user = new UserDto("username", "password", "tpken", "role", "ua");
        user.setId(123L);

        UserDto updatedUser = new UserDto("username", "password", "tpken", "role", "ua");
        when(userManagementService.getUserById(123L)).thenReturn(updatedUser);


        userController.removeGameFromUserListOfGames(gameId, user);


        verify(gameService).setFollowGameOption(gameId, user.getId(), false);
    }

    @Test
    void updateUserLocale_shouldInvokeServiceMethod() {
        String locale = "en_US";
        UserDto user = new UserDto("username", "password", "tpken", "role", "ua");
        user.setId(123L);


        userController.updateUserLocale(locale, user);


        verify(userManagementService).updateLocale(locale, 123L);
    }
}
