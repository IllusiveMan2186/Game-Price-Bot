package com.gpb.stores.schedule;

import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.bean.user.BasicUser;
import com.gpb.stores.service.GameService;
import com.gpb.stores.service.GameStoresService;
import com.gpb.stores.service.NotificationManager;
import com.gpb.stores.service.UserService;
import com.gpb.stores.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@Slf4j
public class GameInfoChangeCheckerConfiguration {


    private final GameService gameService;


    private final GameStoresService gameStoresService;


    private final NotificationManager notificationManager;


    private final UserService userService;

    public GameInfoChangeCheckerConfiguration(GameService gameService, GameStoresService gameStoresService,
                                              NotificationManager notificationManager, UserService userService) {
        this.gameService = gameService;
        this.gameStoresService = gameStoresService;
        this.notificationManager = notificationManager;
        this.userService = userService;
    }

    @Scheduled(cron = Constants.GAME_INFO_CHANGE_CHECKING_TIME)
    public void scheduleSubscribedGameInfoChange() {
        log.info("Check game information changing ");

        List<GameInShop> subscribedGame = gameService.getSubscribedGames();
        List<GameInShop> changedGames = gameStoresService.checkGameInStoreForChange(subscribedGame);
        gameService.changeInfo(changedGames);

        List<BasicUser> users = userService.getUsersOfChangedGameInfo(changedGames);
        for (BasicUser user : users) {
            List<GameInShop> usersChangedGames = gameService.getUsersChangedGames(users.get(0), changedGames);
            notificationManager.sendGameInfoChange(user, usersChangedGames);
        }
    }
}
