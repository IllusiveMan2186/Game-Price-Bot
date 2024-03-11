package com.gpb.web.schedule;

import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.BasicUser;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.service.EmailService;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import com.gpb.web.service.UserService;
import com.gpb.web.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@Slf4j
public class GameInfoChangeCheckerConfiguration {


    private final GameService gameService;


    private final GameStoresService gameStoresService;


    private final EmailService emailService;


    private final UserService userService;

    public GameInfoChangeCheckerConfiguration(GameService gameService, GameStoresService gameStoresService,
                                              EmailService emailService, UserService userService) {
        this.gameService = gameService;
        this.gameStoresService = gameStoresService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Scheduled(cron = Constants.GAME_INFO_CHANGE_CHECKING_TIME)
    public void scheduleSubscribedGameInfoChange() {
        log.info("Check game information changing ");

        List<GameInShop> subscribedGame = gameService.getSubscribedGames();
        List<GameInShop> changedGames = gameStoresService.checkGameInStoreForChange(subscribedGame);
        gameService.changeInfo(changedGames);

        List<BasicUser> users = userService.getUsersOfChangedGameInfo(changedGames);
        List<Long> ids = users.stream()
                .map(BasicUser::getId)
                .toList();
        for (WebUser user : userService.getWebUsers(ids)) {
            List<GameInShop> usersChangedGames = gameService.getUsersChangedGames(users.get(0), changedGames);
            emailService.sendGameInfoChange(user, usersChangedGames);
        }
    }
}
