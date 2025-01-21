package com.gpb.game.unit.service.impl;

import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.common.exception.NotExistingLinkerTokenException;
import com.gpb.common.exception.NotFoundException;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.AccountLinker;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.exception.AccountAlreadyLinkedException;
import com.gpb.game.repository.AccountLinkerRepository;
import com.gpb.game.repository.UserRepository;
import com.gpb.game.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    AccountLinkerRepository accountLinkerRepository;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    void testGetUsersChangedGames_whenSuccess_shouldGetUsers() {
        List<BasicUser> users = new ArrayList<>();
        GameInShop gameInShop1 = GameInShop.builder().id(0).build();
        GameInShop gameInShop2 = GameInShop.builder().id(1).build();
        when(userRepository.findSubscribedUserForChangedGames(List.of(0L, 1L))).thenReturn(users);

        List<BasicUser> result = userService.getUsersOfChangedGameInfo(List.of(gameInShop1, gameInShop2));

        assertEquals(users, result);
    }

    @Test
    void testSubscribeToGame_whenSuccess_shouldCallRepositoryMethod() {
        long userId = 1L;
        long gameId = 101L;


        userService.subscribeToGame(userId, gameId);


        verify(userRepository).addGameToUserListOfGames(userId, gameId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUnsubscribeFromGame_whenSuccess_shouldCallRepositoryMethod() {
        long userId = 1L;
        long gameId = 101L;


        userService.unsubscribeFromGame(userId, gameId);


        verify(userRepository).removeGameFromUserListOfGames(userId, gameId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testLinkUsers_whenSuccess_shouldSaveUserWithNewBAsicUserId() {
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
    void testLinkUsers_whenInvalidToken_shouldThrowException() {
        String token = "invalidToken";

        when(accountLinkerRepository.findById(token)).thenReturn(Optional.empty());

        assertThrows(NotExistingLinkerTokenException.class, () -> userService.linkUsers(token, 1L));
    }

    @Test
    void tesCreateAccountLinkerToken_whenSuccess_shouldReturnToken() {
        long userId = 1L;
        BasicUser user = new BasicUser();
        AccountLinker accountLinker = new AccountLinker();
        accountLinker.setToken("testToken");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountLinkerRepository.save(any(AccountLinker.class))).thenReturn(accountLinker);

        String token = userService.createAccountLinkerToken(userId);

        assertEquals("testToken", token);
        verify(accountLinkerRepository).deleteByUserId(userId);
        verify(userRepository).findById(userId);
        verify(accountLinkerRepository).save(any(AccountLinker.class));
    }

    @Test
    void testLinkUsers_whenTargetUserNotFound_shouldThrowsNotFoundExceptionFor() {
        String token = "token";
        long sourceUserId = 2L;

        when(accountLinkerRepository.findById(token)).thenReturn(Optional.empty());


        NotExistingLinkerTokenException exception = assertThrows(NotExistingLinkerTokenException.class,
                () -> userService.linkUsers(token, sourceUserId));

        assertEquals("app.user.error.messenger.token.not.exist", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testLinkUsers_whenSourceUserNotFound_shouldThrowsNotFoundException() {
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

    @Test
    void testLinkUsers_whenLinkAlreadyLinkedUser_shouldThrowsAccountAlreadyLinkedException() {
        String token = "token";
        long sourceUserId = 2L;

        BasicUser targetUser = new BasicUser();
        targetUser.setId(sourceUserId);
        AccountLinker accountLinker = new AccountLinker(token, targetUser);
        when(accountLinkerRepository.findById(token)).thenReturn(Optional.of(accountLinker));


        assertThrows(AccountAlreadyLinkedException.class, () -> userService.linkUsers(token, sourceUserId));


        verify(userRepository, never()).save(any());
        verify(userRepository, never()).deleteById(anyLong());
    }
}