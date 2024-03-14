package com.gpb.web.service.impl;

import com.gpb.web.exception.NotFoundException;
import com.gpb.web.util.Constants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameStoresServiceImplTest {

    KafkaTemplate<String, Long> kafkaFollowTemplate = mock(KafkaTemplate.class);


    ReplyingKafkaTemplate<String, String, List<String>> requestReplyKafkaTemplate =
            mock(ReplyingKafkaTemplate.class);

    GameStoresServiceImpl gameStoresService =
            new GameStoresServiceImpl(kafkaFollowTemplate, requestReplyKafkaTemplate);

    @Test
    void findGameByName_Success() throws InterruptedException, ExecutionException {
        String gameName = "Test Game";
        List<String> gameIdList = Collections.singletonList("123");
        List<Long> expectedGameIds = Collections.singletonList(123L);
        RequestReplyFuture<String, String, List<String>> requestReplyFuture = mock(RequestReplyFuture.class);
        ConsumerRecord<String, List<String>> response =
                new ConsumerRecord<>("", 1, 1L, "key", gameIdList);
        when(requestReplyKafkaTemplate.sendAndReceive(argThat((ProducerRecord<String, String> record) ->
                Constants.GAME_NAME_SEARCH_TOPIC.equals(record.topic()) && gameName.equals(record.value()))))
                .thenReturn(requestReplyFuture);
        when(requestReplyFuture.get()).thenReturn(response);


        List<Long> actualGameIds = gameStoresService.findGameByName(gameName);

        assertEquals(expectedGameIds, actualGameIds);
    }

    @Test
    void findGameByName_ExceptionThrown() throws InterruptedException, ExecutionException {
        String gameName = "Test Game";
        List<String> gameIdList = Collections.singletonList("123");
        List<Long> expectedGameIds = Collections.singletonList(123L);
        RequestReplyFuture<String, String, List<String>> requestReplyFuture = mock(RequestReplyFuture.class);
        ConsumerRecord<String, List<String>> response =
                new ConsumerRecord<>("", 1, 1L, "key", gameIdList);
        when(requestReplyKafkaTemplate.sendAndReceive(argThat((ProducerRecord<String, String> record) ->
                Constants.GAME_NAME_SEARCH_TOPIC.equals(record.topic()) && gameName.equals(record.value()))))
                .thenReturn(requestReplyFuture);
        when(requestReplyFuture.get()).thenThrow(new InterruptedException());

        assertThrows(NotFoundException.class, () -> gameStoresService.findGameByName(gameName),
                "Error while sending request '%s' for searching of game into topic '%s' with parameter '%s'");
    }

    @Test
    void findGameByUrl_Success() throws InterruptedException, ExecutionException {
        String gameUrl = "http://example.com";
        List<String> gameIdList = Collections.singletonList("123");
        List<Long> expectedGameIds = Collections.singletonList(123L);
        RequestReplyFuture<String, String, List<String>> requestReplyFuture = mock(RequestReplyFuture.class);
        ConsumerRecord<String, List<String>> response =
                new ConsumerRecord<>("", 1, 1L, "key", gameIdList);
        when(requestReplyKafkaTemplate.sendAndReceive(argThat((ProducerRecord<String, String> record) ->
                Constants.GAME_URL_SEARCH_TOPIC.equals(record.topic()) && gameUrl.equals(record.value()))))
                .thenReturn(requestReplyFuture);
        when(requestReplyFuture.get()).thenReturn(response);


        Long actualGameId = gameStoresService.findGameByUrl(gameUrl);

        assertEquals(expectedGameIds.get(0), actualGameId);
    }

    @Test
    void findGameByUrl_ExceptionThrown() throws InterruptedException, ExecutionException {
        String gameUrl = "http://example.com";
        List<String> gameIdList = Collections.singletonList("123");
        RequestReplyFuture<String, String, List<String>> requestReplyFuture = mock(RequestReplyFuture.class);
        ConsumerRecord<String, List<String>> response =
                new ConsumerRecord<>("", 1, 1L, "key", gameIdList);
        when(requestReplyKafkaTemplate.sendAndReceive(argThat((ProducerRecord<String, String> record) ->
                Constants.GAME_URL_SEARCH_TOPIC.equals(record.topic()) && gameUrl.equals(record.value()))))
                .thenReturn(requestReplyFuture);
        when(requestReplyFuture.get()).thenThrow(new InterruptedException());

        assertThrows(NotFoundException.class, () -> gameStoresService.findGameByUrl(gameUrl),
                "Error while sending request '%s' for searching of game into topic '%s' with parameter '%s'");
    }

    @Test
    void subscribeToGame_Success() {
        long gameId = 1L;

        gameStoresService.subscribeToGame(gameId);

        verify(kafkaFollowTemplate).send(argThat((ProducerRecord<String, Long> record) ->
                Constants.GAME_FOLLOW_TOPIC.equals(record.topic()) && record.value() == gameId));
    }

    @Test
    void unsubscribeToGame_Success() {
        long gameId = 1L;

        gameStoresService.unsubscribeFromGame(gameId);

        verify(kafkaFollowTemplate).send(argThat((ProducerRecord<String, Long> record) ->
                Constants.GAME_UNFOLLOW_TOPIC.equals(record.topic()) && record.value() == gameId));
    }
}