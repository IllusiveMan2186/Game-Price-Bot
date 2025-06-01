package com.gpb.game.listener;

import com.gpb.common.entity.event.GameFollowEvent;
import com.gpb.common.entity.game.AddGameInStoreDto;
import com.gpb.common.util.CommonConstants;
import com.gpb.game.entity.game.Game;
import com.gpb.game.service.GameInShopService;
import com.gpb.game.service.GameService;
import com.gpb.game.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka listener for handling game-related events such as follows, unfollow, and removal operations.
 * <p>
 * This listener class listens on multiple topics for various game events:
 * <ul>
 *     <li>{@link CommonConstants#GAME_FOLLOW_TOPIC}: Processes requests to follow a game.</li>
 *     <li>{@link CommonConstants#GAME_UNFOLLOW_TOPIC}: Processes requests to unfollow a game.</li>
 *     <li>{@link CommonConstants#GAME_REMOVE_TOPIC}: Processes requests to remove a game.</li>
 *     <li>{@link CommonConstants#GAME_IN_STORE_REMOVE_TOPIC}: Processes requests to remove a game in store.</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
@AllArgsConstructor
public class GameRequestListener {

    private final GameService gameService;
    private final GameInShopService gameInShopService;
    private final UserService userService;

    /**
     * Processes a game follow event received from Kafka.
     * <p>
     * This method subscribes the user to the specified game and, if the game is not already marked as followed,
     * updates its follow status.
     * </p>
     *
     * @param followRecord the Kafka consumer record containing a {@link GameFollowEvent} as its value.
     */
    @KafkaListener(
            topics = CommonConstants.GAME_FOLLOW_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "followListener"
    )
    @Transactional
    public void listenGameFollow(final ConsumerRecord<String, GameFollowEvent> followRecord) {
        log.info("Received follow request with key '{}' for game event: {}",
                followRecord.key(), followRecord.value());

        final GameFollowEvent event = followRecord.value();
        userService.subscribeToGame(event.getUserId(), event.getGameId());
        final Game game = gameService.getById(event.getGameId());

        if (!game.isFollowed()) {
            gameService.setFollowGameOption(event.getGameId(), true);
        }
    }

    /**
     * Processes a game unfollow event received from Kafka.
     * <p>
     * This method unsubscribes the user from the specified game and, if the game is marked as followed and has no remaining
     * followers, updates its follow status.
     * </p>
     *
     * @param unfollowRecord the Kafka consumer record containing a {@link GameFollowEvent} as its value.
     */
    @KafkaListener(
            topics = CommonConstants.GAME_UNFOLLOW_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "followListener"
    )
    @Transactional
    public void listenGameUnfollow(final ConsumerRecord<String, GameFollowEvent> unfollowRecord) {
        log.info("Received unfollow request with key '{}' for game event: {}",
                unfollowRecord.key(), unfollowRecord.value());

        final GameFollowEvent event = unfollowRecord.value();
        userService.unsubscribeFromGame(event.getUserId(), event.getGameId());
        final Game game = gameService.getByIdWithLoadedUsers(event.getGameId());

        if (game.isFollowed() && game.getUserList().isEmpty()) {
            log.info("Game {} has no more followers", game.getId());
            gameService.setFollowGameOption(event.getGameId(), false);
        }
    }

    /**
     * Processes a game removal event received from Kafka.
     * <p>
     * This method removes the game identified by the given ID from the system.
     * </p>
     *
     * @param removeRecord the Kafka consumer record containing the game ID (as a {@code Long}) to be removed.
     */
    @KafkaListener(
            topics = CommonConstants.GAME_REMOVE_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "gameRemoveListener"
    )
    public void listenGameRemove(final ConsumerRecord<String, Long> removeRecord) {
        log.info("Received remove request with key '{}' for game ID: {}",
                removeRecord.key(), removeRecord.value());
        gameService.removeGame(removeRecord.value());
    }

    /**
     * Processes a game removal event for games in the store received from Kafka.
     * <p>
     * This method removes the game identified by the given ID from the store.
     * </p>
     *
     * @param removeRecord the Kafka consumer record containing the game ID (as a {@code Long}) to be removed from the store.
     */
    @KafkaListener(
            topics = CommonConstants.GAME_IN_STORE_REMOVE_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "gameRemoveListener"
    )
    public void listenGameInStoreRemove(final ConsumerRecord<String, Long> removeRecord) {
        log.info("Received in-store remove request with key '{}' for game ID: {}",
                removeRecord.key(), removeRecord.value());
        gameInShopService.removeGameInStore(removeRecord.value());
    }

    /**
     * Processes a game in store add event from url to registered game received from Kafka.
     * <p>
     * This method add the game in store by url to game by the given ID
     * </p>
     *
     * @param addRecord the Kafka consumer record containing the game ID (as a {@code Long}) to which game in store
     *                  should be added and game in store url.
     */
    @KafkaListener(
            topics = CommonConstants.GAME_IN_STORE_ADD_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "gameInStoreAddListener"
    )
    @Transactional
    public void listenAddGameInStore(final ConsumerRecord<String, AddGameInStoreDto> addRecord) {
        log.info("Received in-store add request from url '{}' for game ID: {}",
                addRecord.value().getUrl(), addRecord.value().getGameId());
        gameInShopService.addGameInStore(addRecord.value().getGameId(), addRecord.value().getUrl());
    }
}
