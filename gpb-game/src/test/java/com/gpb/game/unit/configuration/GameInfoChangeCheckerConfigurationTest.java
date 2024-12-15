package com.gpb.game.unit.configuration;

import com.gpb.game.bean.game.GameInShop;
import com.gpb.game.bean.user.BasicUser;
import com.gpb.game.schedule.GameInfoChangeCheckerConfiguration;
import com.gpb.game.service.GameService;
import com.gpb.game.service.GameStoresService;
import com.gpb.game.service.NotificationManager;
import com.gpb.game.service.UserService;
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
    NotificationManager notificationManager = mock(NotificationManager.class);
    GameStoresService gameStoresService = mock(GameStoresService.class);

    GameInfoChangeCheckerConfiguration changeCheckerConfiguration
            = new GameInfoChangeCheckerConfiguration(gameService, gameStoresService, notificationManager, userService);

    @Test
    void testCreateUser_whenSuccessfully_shouldReturnUser() {
        List<GameInShop> subscribedGame = new ArrayList<>();
        when(gameService.getSubscribedGames()).thenReturn(subscribedGame);
        List<GameInShop> changedGames = new ArrayList<>();
        when(gameStoresService.checkGameInStoreForChange(subscribedGame)).thenReturn(changedGames);
        BasicUser user = new BasicUser();
        user.setId(1);
        List<BasicUser> users = Collections.singletonList(user);
        when(userService.getUsersOfChangedGameInfo(changedGames)).thenReturn(users);
        List<GameInShop> usersChangedGames = new ArrayList<>();
        when(gameService.getUsersChangedGames(users.get(0), changedGames)).thenReturn(usersChangedGames);
        //when(userService.getWebUsers(Collections.singletonList(1L))).thenReturn(webUsers);

        changeCheckerConfiguration.scheduleSubscribedGameInfoChange();

        verify(gameService).changeInfo(changedGames);
        //verify(emailService).sendGameInfoChange(user, usersChangedGames);
    }
}