package com.gpb.web.configuration;

import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.service.EmailService;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import com.gpb.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@Slf4j
public class GameInfoChangeCheckerConfiguration {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameStoresService gameStoresService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    public GameInfoChangeCheckerConfiguration(GameService gameService, GameStoresService gameStoresService,
                                              EmailService emailService, UserService userService) {
        this.gameService = gameService;
        this.gameStoresService = gameStoresService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Scheduled(cron = "0 04 00 * * *")
    public void scheduleSubscribedGameInfoChange() {
        log.info("Check game information changing ");

        List<GameInShop> subscribedGame = gameService.getSubscribedGames();
        List<GameInShop> changedGames = gameStoresService.checkGameInStoreForChange(subscribedGame);
        gameService.changeInfo(changedGames);

        List<WebUser> users = userService.getUsersOfChangedGameInfo(changedGames);
        for (WebUser user : users) {
            List<GameInShop> usersChangedGames = gameService.getUsersChangedGames(users.get(0), changedGames);
            emailService.sendGameInfoChange(user, usersChangedGames);
        }
    }
}
