package com.gpb.game.unit.configuration;

import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.schedule.GameInfoChangeScheduler;
import com.gpb.game.service.GameInShopService;
import com.gpb.game.service.NotificationManager;
import com.gpb.game.service.StoreAggregatorService;
import com.gpb.game.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameInfoChangeSchedulerTest {

    @Mock
    UserService userService;
    @Mock
    GameInShopService gameInShopService;
    @Mock
    NotificationManager notificationManager;
    @Mock
    StoreAggregatorService storeAggregatorService;
    @InjectMocks
    GameInfoChangeScheduler changeCheckerConfiguration;

    @Test
    void testScheduleSubscribedGameInfoChangeEveryDay_whenSuccess_shouldChangeInfoAndSendNotification() {
        List<GameInShop> subscribedGame = new ArrayList<>();
        when(gameInShopService.getSubscribedGames()).thenReturn(subscribedGame);
        List<GameInShop> changedGames = new ArrayList<>();
        when(storeAggregatorService.checkGameInStoreForChange(subscribedGame)).thenReturn(changedGames);
        BasicUser user = new BasicUser();
        user.setId(1);
        List<BasicUser> users = Collections.singletonList(user);
        when(userService.getUsersOfChangedGameInfo(changedGames)).thenReturn(users);
        List<GameInShop> usersChangedGames = new ArrayList<>();
        when(gameInShopService.getUsersChangedGames(users.get(0), changedGames)).thenReturn(usersChangedGames);

        changeCheckerConfiguration.scheduleSubscribedGameInfoChangeEveryDay();

        verify(gameInShopService).changeInfo(changedGames);
        verify(notificationManager).sendGameInfoChange(user, usersChangedGames);
    }

    @Test
    void testScheduleSubscribedGameInfoChangeEveryWeak_whenSuccess_shouldChangeInfo() {
        List<GameInShop> games = new ArrayList<>();
        when(gameInShopService.getAllGamesInShop()).thenReturn(games);
        List<GameInShop> changedGames = new ArrayList<>();
        when(storeAggregatorService.checkGameInStoreForChange(games)).thenReturn(changedGames);


        changeCheckerConfiguration.scheduleGameInfoChangeEveryWeak();

        verify(gameInShopService).changeInfo(changedGames);
    }
}