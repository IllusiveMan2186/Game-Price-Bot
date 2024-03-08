package com.gpb.web.configuration;

import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.BasicUser;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.schedule.GameInfoChangeCheckerConfiguration;
import com.gpb.web.service.EmailService;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameInfoChangeCheckerConfigurationTest {

    UserService userService = mock(UserService.class);

    GameService gameService = mock(GameService.class);

    GameStoresService gameStoresService = mock(GameStoresService.class);

    EmailService emailService = mock(EmailService.class);

    GameInfoChangeCheckerConfiguration changeCheckerConfiguration
            = new GameInfoChangeCheckerConfiguration(gameService, gameStoresService, emailService, userService);

    @Test
    void testCreateUser_whenSuccessfully_shouldReturnUser() {
        List<GameInShop> subscribedGame = new ArrayList<>();
        when(gameService.getSubscribedGames()).thenReturn(subscribedGame);
        List<GameInShop> changedGames = new ArrayList<>();
        when(gameStoresService.checkGameInStoreForChange(subscribedGame)).thenReturn(changedGames);
        WebUser user = new WebUser();
        user.setId(1);
        List<BasicUser> users = Collections.singletonList(user);
        List<WebUser> webUsers = Collections.singletonList(user);
        when(userService.getUsersOfChangedGameInfo(changedGames)).thenReturn(users);
        List<GameInShop> usersChangedGames = new ArrayList<>();
        when(gameService.getUsersChangedGames(users.get(0), changedGames)).thenReturn(usersChangedGames);
        when(userService.getWebUsers(Collections.singletonList(1L))).thenReturn(webUsers);

        changeCheckerConfiguration.scheduleSubscribedGameInfoChange();

        verify(gameService).changeInfo(changedGames);
        verify(emailService).sendGameInfoChange(user, usersChangedGames);
    }
}