package com.gpb.web.configuration;

import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
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

    @Scheduled(cron = "0 13 16 * * *")
    public void scheduleSubscribedGameInfoChange() {
        log.info("Check game information changing ");

        List<GameInShop> subscribedGame = gameService.getSubscribedGames();
        List<GameInShop> changedGames = gameStoresService.checkGameInStoreForChange(subscribedGame);
        gameService.changeInfo(changedGames);

    }
}
