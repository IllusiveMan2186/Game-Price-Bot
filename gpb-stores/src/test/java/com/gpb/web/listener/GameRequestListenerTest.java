package com.gpb.web.listener;

import com.gpb.web.bean.game.Game;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import com.gpb.web.util.Constants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameRequestListenerTest {

    @Mock
    private GameStoresService gameStoresService;

    @Mock
    private GameService gameService;

    @Mock
    private KafkaTemplate<String, List<String>> responseKafkaTemplate;

    @InjectMocks
    private GameRequestListener gameRequestListener;

    @Test
    void testListenGameNameSearch_whenSuccessfully_shouldSendResponse() {
        String gameName = "Test Game";
        List<Game> games = Collections.singletonList(new Game());

        ConsumerRecord<String, String> requestRecord = new ConsumerRecord<>("topic", 0, 0, "key", gameName);
        requestRecord.headers().add(KafkaHeaders.CORRELATION_ID, "1".getBytes());

        ProducerRecord<String, List<String>> expectedResponse
                = new ProducerRecord<>(Constants.GAME_SEARCH_RESPONSE_TOPIC, requestRecord.key(), Collections.singletonList("1"));
        expectedResponse.headers().add(KafkaHeaders.CORRELATION_ID, requestRecord.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value());

        when(gameStoresService.findGameByName(gameName)).thenReturn(games);
        when(gameService.addGames(games)).thenReturn(Collections.singletonList(1L));


        gameRequestListener.listenGameNameSearch(requestRecord);


        verify(gameStoresService, times(1)).findGameByName(gameName);
        verify(responseKafkaTemplate, times(1)).send(expectedResponse);
    }

    @Test
    void testListenGameUrlSearch_whenSuccessfully_shouldSendResponse() {
        String gameUrl = "http://example.com/game";
        Game game = new Game();

        ConsumerRecord<String, String> requestRecord = new ConsumerRecord<>("topic", 0, 0, "key", gameUrl);
        requestRecord.headers().add(KafkaHeaders.CORRELATION_ID, "1".getBytes());

        ProducerRecord<String, List<String>> expectedResponse
                = new ProducerRecord<>(Constants.GAME_SEARCH_RESPONSE_TOPIC, requestRecord.key(), Collections.singletonList("1"));
        expectedResponse.headers().add(KafkaHeaders.CORRELATION_ID, requestRecord.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value());

        when(gameStoresService.findGameByUrl(gameUrl)).thenReturn(game);
        when(gameService.addGames(Collections.singletonList(game))).thenReturn(Collections.singletonList(1L));


        gameRequestListener.listenGameUrlSearch(requestRecord);


        verify(responseKafkaTemplate, times(1)).send(eq(expectedResponse));
    }

    @Test
    void testListenGameFollow_whenSuccessfully_shouldCallMethodSubscribe() {
        long gameId = 123L;
        Game game = new Game();
        ConsumerRecord<String, Long> record = new ConsumerRecord<>("topic", 0, 0, "key", gameId);
        when(gameService.getById(gameId)).thenReturn(game);

        gameRequestListener.listenGameFollow(record);

        verify(gameService, times(1)).getById(gameId);
        verify(gameStoresService, times(1)).subscribeToGame(game);
    }

    @Test
    void testListenGameUnfollow_whenSuccessfully_shouldCallMethodUnsubscribe() {
        long gameId = 123L;
        Game game = new Game();
        ConsumerRecord<String, Long> record = new ConsumerRecord<>("topic", 0, 0, "key", gameId);
        when(gameService.getById(gameId)).thenReturn(game);

        gameRequestListener.listenGameUnfollow(record);

        verify(gameService, times(1)).getById(gameId);
        verify(gameStoresService, times(1)).unsubscribeFromGame(game);
    }
}
