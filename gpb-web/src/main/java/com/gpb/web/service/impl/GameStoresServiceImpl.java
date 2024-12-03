package com.gpb.web.service.impl;

import com.gpb.web.exception.NotFoundException;
import com.gpb.web.service.GameStoresService;
import com.gpb.web.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.KafkaReplyTimeoutException;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class GameStoresServiceImpl implements GameStoresService {

    private static final String REQUEST_MESSAGE_ERROR = "Error while sending request '%s' for searching of game into " +
            "topic '%s' with parameter '%s'";

    private final KafkaTemplate<String, Long> kafkaFollowTemplate;

    private final ReplyingKafkaTemplate<String, String, List<String>> requestReplyKafkaTemplate;

    public GameStoresServiceImpl(KafkaTemplate<String, Long> kafkaFollowTemplate,
                                 ReplyingKafkaTemplate<String, String, List<String>> requestReplyKafkaTemplate) {
        this.kafkaFollowTemplate = kafkaFollowTemplate;
        this.requestReplyKafkaTemplate = requestReplyKafkaTemplate;
    }

    @Override
    public List<Long> findGameByName(String name) {
        log.info(String.format("Search for game by name : '%s'", name));

        return searchForGameRequest(Constants.GAME_NAME_SEARCH_TOPIC, name,
                "app.game.error.name.not.found");
    }

    @Override
    public Long findGameByUrl(String link) {
        log.info(String.format("Search for game by link : '%s'", link));

        return searchForGameRequest(Constants.GAME_URL_SEARCH_TOPIC, link,
                "app.game.error.url.not.found").get(0);
    }

    /**
     * Send search request into store service
     *
     * @param topic            kafka topic for search request on store service
     * @param parameter        parameter for search
     * @param exceptionMessage exception message code that throws if request was not made in time
     * @return list of games
     */
    private List<Long> searchForGameRequest(String topic, String parameter, String exceptionMessage) {
        String key = UUID.randomUUID().toString();
        log.info(String.format("Send request '%s' for searching of game into topic '%s' with parameter '%s'",
                key, topic, parameter));

        ProducerRecord<String, String> searchRequestRecord = new ProducerRecord<>(topic, key, parameter);

        RequestReplyFuture<String, String, List<String>> requestReplyFuture =
                requestReplyKafkaTemplate.sendAndReceive(searchRequestRecord);
        List<Long> games = Collections.emptyList();
        try {
            games = requestReplyFuture.get().value().stream()
                    .map(Long::valueOf)
                    .toList();
            log.info("Received response for request with key: " + key);
        } catch (InterruptedException | ExecutionException | KafkaReplyTimeoutException e) {
            log.error("Error while waiting for response", e);
            throw new NotFoundException(String.format(REQUEST_MESSAGE_ERROR, key, topic, parameter));
        }

        return games;
    }

    @Override
    public void subscribeToGame(long gameId) {
        String key = UUID.randomUUID().toString();
        log.info(String.format("Send request '%s' for follow of game '%s' ", key, gameId));
        kafkaFollowTemplate.send(new ProducerRecord<>(Constants.GAME_FOLLOW_TOPIC, key, gameId));
    }

    @Override
    public void unsubscribeFromGame(long gameId) {
        String key = UUID.randomUUID().toString();
        log.info(String.format("Send request '%s' for unfollow of game '%s' ", key, gameId));
        kafkaFollowTemplate.send(new ProducerRecord<>(Constants.GAME_UNFOLLOW_TOPIC, key, gameId));
    }
}
