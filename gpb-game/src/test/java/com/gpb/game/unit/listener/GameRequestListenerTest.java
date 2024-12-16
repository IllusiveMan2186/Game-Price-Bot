package com.gpb.game.unit.listener;

import com.gpb.game.bean.event.GameFollowEvent;
import com.gpb.game.bean.game.Game;
import com.gpb.game.bean.user.BasicUser;
import com.gpb.game.listener.GameRequestListener;
import com.gpb.game.service.GameService;
import com.gpb.game.service.GameStoresService;
import com.gpb.game.service.UserService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.mock;

class GameRequestListenerTest {


    private final GameService gameService = mock(GameService.class);

    private final UserService userService = mock(UserService.class);

    private final GameRequestListener gameRequestListener
            = new GameRequestListener(gameService, userService);

    @Test
    void testListenGameFollow() {
        long userId = 1L;
        long gameId = 101L;
        GameFollowEvent event = new GameFollowEvent(userId, gameId);
        ConsumerRecord<String, GameFollowEvent> record = new ConsumerRecord<>("topic", 0, 0, "key", event);
        Game game = new Game();

        Mockito.when(gameService.getById(gameId)).thenReturn(game);


        gameRequestListener.listenGameFollow(record);


        Mockito.verify(userService).subscribeToGame(userId, gameId);
        Mockito.verify(gameService).getById(gameId);
        Mockito.verify(gameService).setFollowGameOption(gameId, true);
    }

    @Test
    void testListenGameFollow_whenAlreadyFollowed() {
        long userId = 1L;
        long gameId = 101L;
        GameFollowEvent event = new GameFollowEvent(userId, gameId);
        ConsumerRecord<String, GameFollowEvent> record = new ConsumerRecord<>("topic", 0, 0, "key", event);
        Game game = Game.builder().isFollowed(true).build();

        Mockito.when(gameService.getById(gameId)).thenReturn(game);


        gameRequestListener.listenGameFollow(record);


        Mockito.verify(userService).subscribeToGame(userId, gameId);
        Mockito.verify(gameService).getById(gameId);
        Mockito.verifyNoMoreInteractions(gameService);
    }

    @Test
    void testListenGameUnfollow_whenNoFollowerLeft() {
        long userId = 1L;
        long gameId = 101L;
        GameFollowEvent event = new GameFollowEvent(userId, gameId);
        ConsumerRecord<String, GameFollowEvent> record = new ConsumerRecord<>("topic", 0, 0, "key", event);
        Game game = Game.builder().isFollowed(true).userList(new ArrayList<>()).build();

        Mockito.when(gameService.getById(gameId)).thenReturn(game);


        gameRequestListener.listenGameUnfollow(record);


        Mockito.verify(userService).unsubscribeFromGame(userId, gameId);
        Mockito.verify(gameService).getById(gameId);
        Mockito.verify(gameService).setFollowGameOption(gameId, false);
    }

    @Test
    void testListenGameUnfollowWithRemainingFollower() {
        long userId = 1L;
        long gameId = 101L;
        GameFollowEvent event = new GameFollowEvent(userId, gameId);
        ConsumerRecord<String, GameFollowEvent> record = new ConsumerRecord<>("topic", 0, 0, "key", event);
        Game game = Game.builder().isFollowed(true).userList(Collections.singletonList(new BasicUser())).build();

        Mockito.when(gameService.getById(gameId)).thenReturn(game);


        gameRequestListener.listenGameUnfollow(record);


        Mockito.verify(userService).unsubscribeFromGame(userId, gameId);
        Mockito.verify(gameService).getById(gameId);
        Mockito.verifyNoMoreInteractions(gameService);
    }

    @Test
    void testListenGameRemove() {
        long gameId = 101L;
        ConsumerRecord<String, Long> record = new ConsumerRecord<>("topic", 0, 0, "key", gameId);


        gameRequestListener.listenGameRemove(record);


        Mockito.verify(gameService).removeGame(gameId);
    }

    @Test
    void testListenGameInStoreRemove() {
        long gameId = 102L;
        ConsumerRecord<String, Long> record = new ConsumerRecord<>("topic", 0, 0, "key", gameId);


        gameRequestListener.listenGameInStoreRemove(record);


        Mockito.verify(gameService).removeGameInStore(gameId);
    }
}
