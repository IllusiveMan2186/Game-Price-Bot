package com.gpb.web.listener;

import com.gpb.web.bean.game.Game;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameRequestListenerTest {

    @Mock
    private GameStoresService gameStoresService;

    @Mock
    private GameService gameService;

    @Mock
    private KafkaTemplate<String, List<Game>> responseKafkaTemplate;

    @InjectMocks
    private GameRequestListener gameRequestListener;

    @Test
    public void testListenGameNameSearchSuccessfullyShouldSendResponse() {
        String gameName = "Test Game";
        List<Game> games = Collections.singletonList(new Game());
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0, "key", gameName);
        when(gameStoresService.findGameByName(gameName)).thenReturn(games);

        gameRequestListener.listenGameNameSearch(record);

        verify(gameStoresService, times(1)).findGameByName(gameName);
        verify(responseKafkaTemplate, times(1)).send(any(), any(), eq(games));
    }

    @Test
    public void testListenGameUrlSearchSuccessfullyShouldSendResponse() {
        String gameUrl = "http://example.com/game";
        Game game = new Game();
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0, "key", gameUrl);
        when(gameStoresService.findGameByUrl(gameUrl)).thenReturn(game);

        gameRequestListener.listenGameUrlSearch(record);

        verify(gameStoresService, times(1)).findGameByUrl(gameUrl);
        verify(responseKafkaTemplate, times(1)).send(any(), any(), eq(Collections.singletonList(game)));
    }

    @Test
    public void testListenGameFollowSuccessfullyShouldCallMethodSubscribe() {
        long gameId = 123L;
        Game game = new Game();
        ConsumerRecord<String, Long> record = new ConsumerRecord<>("topic", 0, 0, "key", gameId);
        when(gameService.getById(gameId)).thenReturn(game);

        gameRequestListener.listenGameFollow(record);

        verify(gameService, times(1)).getById(gameId);
        verify(gameStoresService, times(1)).subscribeToGame(game);
    }

    @Test
    public void testListenGameUnfollowSuccessfullyShouldCallMethodUnsubscribe() {
        long gameId = 123L;
        Game game = new Game();
        ConsumerRecord<String, Long> record = new ConsumerRecord<>("topic", 0, 0, "key", gameId);
        when(gameService.getById(gameId)).thenReturn(game);

        gameRequestListener.listenGameUnfollow(record);

        verify(gameService, times(1)).getById(gameId);
        verify(gameStoresService, times(1)).unsubscribeFromGame(game);
    }
}
