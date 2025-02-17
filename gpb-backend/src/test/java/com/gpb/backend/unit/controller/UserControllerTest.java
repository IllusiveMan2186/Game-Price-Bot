package com.gpb.backend.unit.controller;

import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.controller.UserController;
import com.gpb.backend.entity.dto.EmailRequestDto;
import com.gpb.backend.entity.dto.LocaleRequestDto;
import com.gpb.backend.entity.dto.PasswordChangeDto;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.GameService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.service.UserManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserManagementService userManagementService;
    @Mock
    private UserAuthenticationService userAuthenticationService;

    @Mock
    private GameService gameService;

    @Mock
    private UserActivationService userActivationService;

    @Mock
    private UserAuthenticationProvider userAuthenticationProvider;

    @InjectMocks
    private UserController userController;

    @Test
    void testUpdateUserPassword_whenSuccess_shouldReturnUpdatedUser() {
        char[] oldPassword = "oldPassword123".toCharArray();
        char[] newPassword = "newPassword123".toCharArray();
        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setOldPassword(oldPassword);
        passwordChangeDto.setNewPassword(newPassword);
        UserDto user = new UserDto("username", "password", "token", "role", "ua");
        user.setId(123L);

        UserDto updatedUser = new UserDto("username", "password", "token", "role", "ua");
        when(userAuthenticationService.updateUserPassword(oldPassword, newPassword, user)).thenReturn(updatedUser);


        UserDto result = userController.updateUserPassword(passwordChangeDto, user);


        assertNotNull(result);
        verify(userAuthenticationService).updateUserPassword(oldPassword, newPassword, user);
    }

    @Test
    void testAddGameToUserListOfGames_whenSuccess_shouldReturnUpdatedUser() {
        long gameId = 1L;
        UserDto user = new UserDto("username", "password", "token", "role", "ua");
        user.setBasicUserId(123L);


        userController.addGameToUserListOfGames(gameId, user);


        verify(gameService).setFollowGameOption(gameId, user.getBasicUserId(), true);
    }

    @Test
    void testRemoveGameFromUserListOfGames_whenSuccess_shouldReturnUpdatedUser() {
        long gameId = 1L;
        UserDto user = new UserDto("username", "password", "token", "role", "ua");
        user.setBasicUserId(123L);


        userController.removeGameFromUserListOfGames(gameId, user);


        verify(gameService).setFollowGameOption(gameId, user.getBasicUserId(), false);
    }

    @Test
    void testUpdateUserLocale_whenSuccess_shouldInvokeServiceMethod() {
        String locale = "en_US";
        UserDto user = new UserDto("username", "password", "token", "role", "ua");
        user.setId(123L);


        userController.updateUserLocale(new LocaleRequestDto(locale), user);


        verify(userManagementService).updateLocale(locale, 123L);
    }
}
