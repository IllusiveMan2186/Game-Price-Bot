package com.gpb.web.service.impl;

import com.gpb.web.bean.game.Game;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.service.GameStoresService;
import com.gpb.web.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.openqa.selenium.devtools.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GameStoresServiceImpl implements GameStoresService {

    private static final String REQUEST_MESSAGE_ERROR = "Error while sending request '%s' for searching of game into " +
            "topic '%s' with parameter '%s'";

    @Autowired
    private KafkaTemplate<String, Long> kafkaFollowTemplate;

    @Autowired
    private ReplyingKafkaTemplate<String, String, List<String>> requestReplyKafkaTemplate;

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
        String correlationId = UUID.randomUUID().toString();
        log.info(String.format("Send request '%s' for searching of game into topic '%s' with parameter '%s'",
                correlationId, topic, parameter));

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, correlationId, parameter);
        //record.headers().add(new RecordHeader(KafkaHeaders.CORRELATION_ID, correlationId.getBytes()));
        //record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, Constants.GAME_SEARCH_RESPONSE_TOPIC.getBytes()));

        System.out.println(record);
        RequestReplyFuture<String, String, List<String>> requestReplyFuture = requestReplyKafkaTemplate.sendAndReceive(record);
        //CompletableFuture<List<Long>> completableFuture = new CompletableFuture<>();
        List<Long> games = new ArrayList<>();
        try {
            //requestReplyFuture.getSendFuture().get(Constants.SEARCH_REQUEST_WAITING_TIME, TimeUnit.SECONDS); // send ok
            SendResult<String, String> sendResult = requestReplyFuture.getSendFuture().get(Constants.SEARCH_REQUEST_WAITING_TIME, TimeUnit.SECONDS);
            System.out.println("Sent ok: " + sendResult.getRecordMetadata());
            games = requestReplyFuture.get().value().stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList());;
            log.info("Received response for request with correlationId: " + correlationId);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error while waiting for response", e);
            throwNotFoundException(exceptionMessage, String.format(REQUEST_MESSAGE_ERROR, correlationId, topic, parameter));
            return null;
        }

        return games;
    }

    private void throwNotFoundException(String exceptionMessage, String logMessage) {
        log.error(logMessage);
        throw new NotFoundException(exceptionMessage);
    }

    @Override
    public void subscribeToGame(long gameId) {
        kafkaFollowTemplate.send(new ProducerRecord<>(Constants.GAME_FOLLOW_TOPIC, UUID.randomUUID().toString(), gameId));
    }

    @Override
    public void unsubscribeFromGame(long gameId) {
        kafkaFollowTemplate.send(new ProducerRecord<>(Constants.GAME_UNFOLLOW_TOPIC, UUID.randomUUID().toString(), gameId));
    }

    @KafkaListener(topics = Constants.GAME_SEARCH_RESPONSE_TOPIC, groupId = "gpb")
    public void listen(ConsumerRecord<String, List<Long>> record) {
        String correlationId = record.key();
        List<Long> response = record.value();
        log.info("Listen response for search request " + correlationId);

    }
}
