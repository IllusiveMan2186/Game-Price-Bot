package com.gpb.web.service.impl;

import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.BasicUser;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.repository.UserRepository;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.gpb.web.util.Constants.USER_ROLE;
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