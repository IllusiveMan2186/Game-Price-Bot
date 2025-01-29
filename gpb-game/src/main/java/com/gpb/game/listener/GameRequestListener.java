package com.gpb.game.listener;

import com.gpb.common.entity.event.GameFollowEvent;
import com.gpb.common.util.CommonConstants;
import com.gpb.game.entity.game.Game;
import com.gpb.game.service.GameService;
import com.gpb.game.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@AllArgsConstructor
public class GameRequestListener {

    private final GameService gameService;
    private final UserService userService;

    @KafkaListener(topics = CommonConstants.GAME_FOLLOW_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "followListener")
    @Transactional
    public void listenGameFollow(ConsumerRecord<String, GameFollowEvent> followRecord) {
        log.info("Request {} for follow for game {}", followRecord.key(), followRecord.value());

        userService.subscribeToGame(followRecord.value().getUserId(), followRecord.value().getGameId());
        Game game = gameService.getById(followRecord.value().getGameId());

        if (!game.isFollowed()) {
            gameService.setFollowGameOption(followRecord.value().getGameId(), true);
        }
    }


    @KafkaListener(topics = CommonConstants.GAME_UNFOLLOW_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "followListener")
    @Transactional
    public void listenGameUnfollow(ConsumerRecord<String, GameFollowEvent> unfollowRecord) {
        log.info("Request {} for unfollow for game {}", unfollowRecord.key(), unfollowRecord.value());

        userService.unsubscribeFromGame(unfollowRecord.value().getUserId(), unfollowRecord.value().getGameId());
        Game game = gameService.getById(unfollowRecord.value().getGameId());

        if (game.isFollowed() && game.getUserList().isEmpty()) {
            log.info("Game {} have no more followers", game.getId());
            gameService.setFollowGameOption(unfollowRecord.value().getGameId(), false);
        }
    }

    @KafkaListener(topics = CommonConstants.GAME_REMOVE_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "gameRemoveListener")
    @Transactional
    public void listenGameRemove(ConsumerRecord<String, Long> removeRecord) {
        log.info("Request {} for removing game {}", removeRecord.key(), removeRecord.value());
        gameService.removeGame(removeRecord.value());
    }

    @KafkaListener(topics = CommonConstants.GAME_IN_STORE_REMOVE_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "gameRemoveListener")
    @Transactional
    public void listenGameInStoreRemove(ConsumerRecord<String, Long> removeRecord) {
        log.info("Request {} for removing game in store {}", removeRecord.key(), removeRecord.value());
        gameService.removeGameInStore(removeRecord.value());
    }
}
