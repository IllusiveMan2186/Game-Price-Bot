package com.gpb.game.schedule;

import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.service.GameInShopService;
import com.gpb.game.service.GameStoresService;
import com.gpb.game.service.NotificationManager;
import com.gpb.game.service.UserService;
import com.gpb.game.util.Constants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Scheduler responsible for periodically checking for changes in game information
 * and performing the necessary updates and notifications.
 * <p>
 * This scheduler performs two primary tasks:
 * <ul>
 *   <li>
 *     <strong>Daily Task:</strong> Checks for changes in the information of games that users have subscribed to.
 *     If any changes are detected, the system updates the game information and notifies the affected users.
 *   </li>
 *   <li>
 *     <strong>Weekly Task:</strong> Checks for changes in the information of all games available in the shop
 *     and updates the game details accordingly.
 *   </li>
 * </ul>
 * </p>
 */
@Component
@Slf4j
@AllArgsConstructor
public class GameInfoChangeScheduler {

    private final GameInShopService gameInShopService;
    private final GameStoresService gameStoresService;
    private final NotificationManager notificationManager;
    private final UserService userService;

    /**
     * Scheduled task that runs daily to check for changes in game information for subscribed games.
     * <p>
     * The daily workflow includes:
     * <ol>
     *   <li>Retrieving the list of games that users have subscribed to.</li>
     *   <li>Checking these games in the store for any updates or changes.</li>
     *   <li>Updating the game information in the system if changes are detected.</li>
     *   <li>Identifying users who follow the changed games.</li>
     *   <li>Sending notifications to each user with details of the specific games that have changed.</li>
     * </ol>
     * </p>
     */
    @Transactional
    @Scheduled(cron = Constants.GAME_INFO_CHANGE_DAILY)
    public void scheduleSubscribedGameInfoChangeEveryDay() {
        log.info("Starting daily check for game information changes for subscribed games.");

        final List<GameInShop> subscribedGames = gameInShopService.getSubscribedGames();
        final List<GameInShop> changedGames = gameStoresService.checkGameInStoreForChange(subscribedGames);

        log.info("Amount of games that information was changed {}", changedGames.size());

        gameInShopService.changeInfo(changedGames);

        final List<BasicUser> users = userService.getUsersOfChangedGameInfo(changedGames);
        for (final BasicUser user : users) {
            final List<GameInShop> usersChangedGames = gameInShopService.getUsersChangedGames(user, changedGames);
            notificationManager.sendGameInfoChange(user, usersChangedGames);
        }

        log.info("Completed daily game info change check. Notified {} users.", users.size());
    }

    /**
     * Scheduled task that runs weekly to check for changes in game information for all games in the shop.
     * <p>
     * The weekly workflow includes:
     * <ol>
     *   <li>Retrieving all games available in the shop.</li>
     *   <li>Checking these games for any changes in their information from the store.</li>
     *   <li>Updating the game information in the system if any changes are detected.</li>
     * </ol>
     * </p>
     */
    @Transactional
    @Scheduled(cron = Constants.GAME_INFO_CHANGE_WEAKLY)
    public void scheduleGameInfoChangeEveryWeak() {
        log.info("Starting weekly check for game information changes for all games in the shop.");

        final List<GameInShop> games = gameInShopService.getAllGamesInShop();
        final List<GameInShop> changedGames = gameStoresService.checkGameInStoreForChange(games);

        gameInShopService.changeInfo(changedGames);

        log.info("Completed weekly game info change check. Updated {} games.", changedGames.size());
    }
}
