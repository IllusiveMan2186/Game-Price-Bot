package com.gpb.stores.service.impl;

import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.bean.user.BasicUser;
import com.gpb.stores.bean.user.WebUser;
import com.gpb.stores.repository.UserRepository;
import com.gpb.stores.repository.WebUserRepository;
import com.gpb.stores.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    WebUserRepository webUserRepository = mock(WebUserRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    UserService userService = new UserServiceImpl(userRepository, webUserRepository);
    private final WebUser user = new WebUser("email", new Locale("ua"));

    @Test
    void testGetUsersChangedGames_whenSuccessfully_thenShouldGetUsers() {
        List<BasicUser> users = new ArrayList<>();
        GameInShop gameInShop1 = GameInShop.builder().id(0).build();
        GameInShop gameInShop2 = GameInShop.builder().id(1).build();
        when(userRepository.findSubscribedUserForChangedGames(List.of(0L, 1L))).thenReturn(users);

        List<BasicUser> result = userService.getUsersOfChangedGameInfo(List.of(gameInShop1, gameInShop2));

        assertEquals(users, result);
    }

    @Test
    void testGetWebUsers_whenSuccessfully_thenShouldGetUsers() {
        List<WebUser> users = new ArrayList<>();
        List<Long> ids = List.of(0L);
        when(webUserRepository.findAllByIdIn(ids)).thenReturn(users);

        List<WebUser> result = userService.getWebUsers(ids);

        assertEquals(users, result);
    }
}