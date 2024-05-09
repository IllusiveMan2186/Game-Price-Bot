package com.gpb.telegram.service.impl;

import com.gpb.telegram.exception.NotFoundException;
import com.gpb.telegram.service.GameStoresService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@AllArgsConstructor
public class GameStoresServiceImpl implements GameStoresService {

    private static final String REQUEST_MESSAGE_ERROR = "Error while sending request '%s' for searching of game into " +
            "topic '%s' with parameter '%s'";

    private final ReplyingKafkaTemplate<String, String, List<String>> requestReplyKafkaTemplate;
    private final KafkaTemplate<String, Long> kafkaFollowTemplate;

    @Override
    public List<Long> findGameByName(String name) {
        log.info(String.format("Search for game by name : '%s'", name));
        return searchForGameRequest(Constants.GAME_NAME_SEARCH_TOPIC, name);
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

    /**
     * Send search request into store service
     *
     * @param topic            kafka topic for search request on store service
     * @param parameter        parameter for search
     * @return list of games
     */
    private List<Long> searchForGameRequest(String topic, String parameter) {
        String key = UUID.randomUUID().toString();
        log.info(String.format("Send request '%s' for searching of game into topic '%s' with parameter '%s'",
                key, topic, parameter));

        ProducerRecord<String, String> searchRequestRecord = new ProducerRecord<>(topic, key, parameter);
        RequestReplyFuture<String, String, List<String>> requestReplyFuture =
                requestReplyKafkaTemplate.sendAndReceive(searchRequestRecord);
        List<Long> games;
        try {
            games = requestReplyFuture.get().value().stream()
                    .map(Long::valueOf)
                    .toList();
            log.info("Received response for request with key: " + key);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while waiting for response", e);
            throw new NotFoundException(String.format(REQUEST_MESSAGE_ERROR, key, topic, parameter));
        }
        return games;
    }
}
