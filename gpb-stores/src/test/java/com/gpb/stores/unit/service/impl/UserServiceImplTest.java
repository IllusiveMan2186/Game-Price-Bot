package com.gpb.stores.unit.service.impl;

import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.bean.user.BasicUser;
import com.gpb.stores.repository.UserRepository;
import com.gpb.stores.service.UserService;
import com.gpb.stores.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    UserRepository userRepository = mock(UserRepository.class);
    UserService userService = new UserServiceImpl(userRepository);

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
}