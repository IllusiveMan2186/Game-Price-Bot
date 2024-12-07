package com.gpb.stores.listener;

import com.gpb.stores.bean.GameFollowEvent;
import com.gpb.stores.bean.game.Game;
import com.gpb.stores.service.GameService;
import com.gpb.stores.service.GameStoresService;
import com.gpb.stores.service.UserService;
import com.gpb.stores.util.Constants;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class GameRequestListener {


    private final GameStoresService gameStoresService;

    private final GameService gameService;
    private final UserService userService;

    public GameRequestListener(GameStoresService gameStoresService, GameService gameService, UserService userService) {
        this.gameStoresService = gameStoresService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @KafkaListener(topics = Constants.GAME_FOLLOW_TOPIC, groupId = Constants.GPB_KAFKA_GROUP_ID)
    @Transactional
    public void listenGameFollow(ConsumerRecord<String, GameFollowEvent> followRecord) {
        log.info("Request {} for follow for game {}", followRecord.key(), followRecord.value());

        userService.subscribeToGame(followRecord.value().getUserId(), followRecord.value().getGameId());
        Game game = gameService.getById(followRecord.value().getGameId());

        if (!game.isFollowed()) {
            gameService.setFollowGameOption(followRecord.value().getGameId(), true);
            gameStoresService.subscribeToGame(game);
        }
    }


    @KafkaListener(topics = Constants.GAME_UNFOLLOW_TOPIC, groupId = Constants.GPB_KAFKA_GROUP_ID)
    @Transactional
    public void listenGameUnfollow(ConsumerRecord<String, GameFollowEvent> unfollowRecord) {
        log.info("Request {} for unfollow for game {}", unfollowRecord.key(), unfollowRecord.value());

        userService.unsubscribeFromGame(unfollowRecord.value().getUserId(), unfollowRecord.value().getGameId());
        Game game = gameService.getById(unfollowRecord.value().getGameId());

        if (game.isFollowed() && game.getUserList().isEmpty()) {
            log.info("Game {} have no more followers", game.getId());
            gameService.setFollowGameOption(unfollowRecord.value().getGameId(), false);
            gameStoresService.unsubscribeFromGame(game);
        }
    }

    @KafkaListener(topics = Constants.GAME_REMOVE_TOPIC, groupId = Constants.GPB_KAFKA_GROUP_ID)
    @Transactional
    public void listenGameRemove(ConsumerRecord<String, Long> unfollowRecord) {
        log.info("Request {} for removing game {}", unfollowRecord.key(), unfollowRecord.value());
        gameService.removeGame(unfollowRecord.value());
    }

    @KafkaListener(topics = Constants.GAME_IN_STORE_REMOVE_TOPIC, groupId = Constants.GPB_KAFKA_GROUP_ID)
    @Transactional
    public void listenGameInStoreRemove(ConsumerRecord<String, Long> unfollowRecord) {
        log.info("Request {} for removing game in store {}", unfollowRecord.key(), unfollowRecord.value());
        gameService.removeGameInStore(unfollowRecord.value());
    }
}
