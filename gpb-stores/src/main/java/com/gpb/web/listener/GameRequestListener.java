package com.gpb.web.listener;

import com.gpb.web.bean.game.Game;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import com.gpb.web.util.Constants;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class GameRequestListener {


    private final GameStoresService gameStoresService;

    private final GameService gameService;

    private final KafkaTemplate<String, List<String>> responseKafkaTemplate;

    public GameRequestListener(GameStoresService gameStoresService, GameService gameService, KafkaTemplate<String,
            List<String>> responseKafkaTemplate) {
        this.gameStoresService = gameStoresService;
        this.gameService = gameService;
        this.responseKafkaTemplate = responseKafkaTemplate;
    }

    @KafkaListener(topics = Constants.GAME_NAME_SEARCH_TOPIC, groupId = Constants.GPB_KAFKA_GROUP_ID,
            containerFactory = Constants.SEARCH_REPLY_LISTENER_CONTAINER_FACTORY)
    public void listenGameNameSearch(ConsumerRecord<String, String> requestRecord) {
        log.info(String.format("Request '%s' for searching of game with name '%s'", requestRecord.key(), requestRecord.value()));
        List<Game> games = gameStoresService.findGameByName(requestRecord.value());
        List<Long> gamesId = gameService.addGames(games);
        sendGamesSearchResponse(gamesId, requestRecord);
    }

    @KafkaListener(topics = Constants.GAME_URL_SEARCH_TOPIC, groupId = Constants.GPB_KAFKA_GROUP_ID,
            containerFactory = Constants.SEARCH_REPLY_LISTENER_CONTAINER_FACTORY)
    public void listenGameUrlSearch(ConsumerRecord<String, String> requestRecord) {
        log.info(String.format("Request '%s' for searching of game with url '%s'", requestRecord.key(), requestRecord.value()));
        Game game = gameStoresService.findGameByUrl(requestRecord.value());
        List<Long> gamesId =  gameService.addGames(Collections.singletonList(game));
        sendGamesSearchResponse(gamesId, requestRecord);
    }

    private void sendGamesSearchResponse(List<Long> gameIds, ConsumerRecord<String, String> requestRecord) {
        List<String> gamesId = gameIds.stream()
                .map(Object::toString)
                .toList();

        ProducerRecord<String, List<String>> response
                = new ProducerRecord<>(Constants.GAME_SEARCH_RESPONSE_TOPIC, requestRecord.key(), gamesId);
        response.headers().add(KafkaHeaders.CORRELATION_ID, requestRecord.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value());

        responseKafkaTemplate.send(response);
    }

    @KafkaListener(topics = "gpb_game_follow", groupId = "gpb",
            containerFactory = Constants.FOLLOW_REPLY_LISTENER_CONTAINER_FACTORY)
    @Transactional
    public void listenGameFollow(ConsumerRecord<String, Long> followRecord) {
        log.info(String.format("Request '%s' for follow for game '%s'", followRecord.key(), followRecord.value()));
        Game game = gameService.getById(followRecord.value());
        gameStoresService.subscribeToGame(game);
    }


    @KafkaListener(topics = "gpb_game_unfollow", groupId = "gpb",
            containerFactory = Constants.FOLLOW_REPLY_LISTENER_CONTAINER_FACTORY)
    @Transactional
    public void listenGameUnfollow(ConsumerRecord<String, Long> unfollowRecord) {
        log.info(String.format("Request '%s' for unfollow for game '%s'", unfollowRecord.key(), unfollowRecord.value()));
        Game game = gameService.getById(unfollowRecord.value());
        gameStoresService.unsubscribeFromGame(game);
    }
}
