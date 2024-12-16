package com.gpb.game.schedule;

import com.gpb.game.bean.game.GameInShop;
import com.gpb.game.bean.user.BasicUser;
import com.gpb.game.service.GameService;
import com.gpb.game.service.GameStoresService;
import com.gpb.game.service.NotificationManager;
import com.gpb.game.service.UserService;
import com.gpb.game.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@Slf4j
@AllArgsConstructor
public class GameInfoChangeCheckerConfiguration {

    private final GameService gameService;
    private final GameStoresService gameStoresService;
    private final NotificationManager notificationManager;
    private final UserService userService;

    @Scheduled(cron = Constants.GAME_INFO_CHANGE_DAILY)
    public void scheduleSubscribedGameInfoChangeEveryDay() {
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

    @Scheduled(cron = Constants.GAME_INFO_CHANGE_WEAKLY)
    public void scheduleGameInfoChangeEveryWeak() {
        log.info("Check game information changing ");

        List<GameInShop> games = gameService.getAllGamesInShop();
        List<GameInShop> changedGames = gameStoresService.checkGameInStoreForChange(games);
        gameService.changeInfo(changedGames);
    }
}
