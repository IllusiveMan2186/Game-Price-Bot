package com.gpb.stores.unit.service.impl;

import com.gpb.stores.bean.game.Game;
import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.bean.user.AccountLinker;
import com.gpb.stores.bean.user.BasicUser;
import com.gpb.stores.bean.user.UserNotificationType;
import com.gpb.stores.exception.NotExistingMessengerActivationTokenException;
import com.gpb.stores.exception.NotFoundException;
import com.gpb.stores.repository.AccountLinkerRepository;
import com.gpb.stores.repository.UserRepository;
import com.gpb.stores.service.UserService;
import com.gpb.stores.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    UserRepository userRepository = mock(UserRepository.class);
    AccountLinkerRepository accountLinkerRepository = mock(AccountLinkerRepository.class);
    UserService userService = new UserServiceImpl(accountLinkerRepository, userRepository);

    @Test
    void testGetUsersChangedGames_whenSuccessfully_shouldGetUsers() {
        List<BasicUser> users = new ArrayList<>();
        GameInShop gameInShop1 = GameInShop.builder().id(0).build();
        GameInShop gameInShop2 = GameInShop.builder().id(1).build();
        when(userRepository.findSubscribedUserForChangedGames(List.of(0L, 1L))).thenReturn(users);

        List<BasicUser> result = userService.getUsersOfChangedGameInfo(List.of(gameInShop1, gameInShop2));

        assertEquals(users, result);
    }

    @Test
    void testSubscribeToGame_whenSuccessfully_shouldCallRepositoryMethod() {
        long userId = 1L;
        long gameId = 101L;


        userService.subscribeToGame(userId, gameId);


        verify(userRepository).addGameToUserListOfGames(userId, gameId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUnsubscribeFromGame_whenSuccessfully_shouldCallRepositoryMethod() {
        long userId = 1L;
        long gameId = 101L;


        userService.unsubscribeFromGame(userId, gameId);


        verify(userRepository).removeGameFromUserListOfGames(userId, gameId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testLinkUsersSuccessfully() {
        long sourceUserId = 456L;
        String token = "token";

        List<Game> sourceGameList = List.of(new Game());
        List<UserNotificationType> sourceNotificationTypes = List.of(UserNotificationType.TELEGRAM);

        BasicUser sourceUser = new BasicUser(sourceUserId, sourceGameList, sourceNotificationTypes);

        List<Game> targetGameList = new ArrayList<>(List.of(new Game()));
        List<UserNotificationType> targetNotificationTypes = new ArrayList<>(List.of(UserNotificationType.EMAIL));

        BasicUser targetUser = new BasicUser(1L, targetGameList, targetNotificationTypes);
        AccountLinker accountLinker = new AccountLinker(token, targetUser);
        when(accountLinkerRepository.findById(token)).thenReturn(Optional.of(accountLinker));
        when(userRepository.findById(sourceUserId)).thenReturn(Optional.of(sourceUser));


        userService.linkUsers(token, sourceUserId);


        assertTrue(targetUser.getGameList().containsAll(sourceGameList));
        assertTrue(targetUser.getNotificationTypes().containsAll(sourceNotificationTypes));

        verify(userRepository).save(targetUser);
        verify(userRepository).deleteById(sourceUserId);
    }

    @Test
    void linkUsers_shouldThrowExceptionForInvalidToken() {
        String token = "invalidToken";

        when(accountLinkerRepository.findById(token)).thenReturn(Optional.empty());

        assertThrows(NotExistingMessengerActivationTokenException.class, () -> userService.linkUsers(token, 1L));
    }

    @Test
    void getAccountLinkerToken_shouldReturnToken() {
        long userId = 1L;
        BasicUser user = new BasicUser();
        AccountLinker accountLinker = new AccountLinker();
        accountLinker.setToken("testToken");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountLinkerRepository.save(any(AccountLinker.class))).thenReturn(accountLinker);

        String token = userService.getAccountLinkerToken(userId);

        assertEquals("testToken", token);
        verify(userRepository).findById(userId);
        verify(accountLinkerRepository).save(any(AccountLinker.class));
    }

    @Test
    void testLinkUsersThrowsNotFoundExceptionForTargetUser() {
        String token = "token";
        long sourceUserId = 2L;

        when(accountLinkerRepository.findById(token)).thenReturn(Optional.empty());


        NotExistingMessengerActivationTokenException exception = assertThrows(NotExistingMessengerActivationTokenException.class,
                () -> userService.linkUsers(token, sourceUserId));

        assertEquals("app.user.error.messenger.token.not.exist", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testLinkUsersThrowsNotFoundExceptionForSourceUser() {
        String token = "token";
        long sourceUserId = 2L;

        BasicUser targetUser = new BasicUser();
        AccountLinker accountLinker = new AccountLinker(token, targetUser);
        when(accountLinkerRepository.findById(token)).thenReturn(Optional.of(accountLinker));
        when(userRepository.findById(sourceUserId)).thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.linkUsers(token, sourceUserId));

        assertEquals("Source user not found with ID: " + sourceUserId, exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).deleteById(anyLong());
    }
}