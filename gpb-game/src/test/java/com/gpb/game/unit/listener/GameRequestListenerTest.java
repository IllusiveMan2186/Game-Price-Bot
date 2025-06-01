package com.gpb.game.unit.listener;

import com.gpb.common.entity.event.GameFollowEvent;
import com.gpb.common.entity.game.AddGameInStoreDto;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.listener.GameRequestListener;
import com.gpb.game.service.GameInShopService;
import com.gpb.game.service.GameService;
import com.gpb.game.service.UserService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class GameRequestListenerTest {

    @Mock
    private GameService gameService;
    @Mock
    private GameInShopService gameInShopService;
    @Mock
    private UserService userService;

    @InjectMocks
    private GameRequestListener gameRequestListener;

    @Test
    void testListenGameFollow_whenGameNotFollowed_shouldSubscribeForUserAndSetFollowGameOptionTrue() {
        long userId = 1L;
        long gameId = 101L;
        GameFollowEvent event = new GameFollowEvent(userId, gameId);
        ConsumerRecord<String, GameFollowEvent> gameFollow = new ConsumerRecord<>("topic", 0, 0, "key", event);
        Game game = new Game();

        Mockito.when(gameService.getById(gameId)).thenReturn(game);


        gameRequestListener.listenGameFollow(gameFollow);


        Mockito.verify(userService).subscribeToGame(userId, gameId);
        Mockito.verify(gameService).getById(gameId);
        Mockito.verify(gameService).setFollowGameOption(gameId, true);
    }

    @Test
    void testListenGameFollow_whenAlreadyFollowed_shouldSubscribeForUserAndNotCallGameService() {
        long userId = 1L;
        long gameId = 101L;
        GameFollowEvent event = new GameFollowEvent(userId, gameId);
        ConsumerRecord<String, GameFollowEvent> gameFollow = new ConsumerRecord<>("topic", 0, 0, "key", event);
        Game game = Game.builder().isFollowed(true).build();

        Mockito.when(gameService.getById(gameId)).thenReturn(game);


        gameRequestListener.listenGameFollow(gameFollow);


        Mockito.verify(userService).subscribeToGame(userId, gameId);
        Mockito.verify(gameService).getById(gameId);
        Mockito.verifyNoMoreInteractions(gameService);
    }

    @Test
    void testListenGameUnfollow_whenNoFollowerLeft_shouldUnsubscribeFromUserAndSetFollowGameOptionFalse() {
        long userId = 1L;
        long gameId = 101L;
        GameFollowEvent event = new GameFollowEvent(userId, gameId);
        ConsumerRecord<String, GameFollowEvent> gameFollow = new ConsumerRecord<>("topic", 0, 0, "key", event);
        Game game = Game.builder().isFollowed(true).userList(new ArrayList<>()).build();

        Mockito.when(gameService.getByIdWithLoadedUsers(gameId)).thenReturn(game);


        gameRequestListener.listenGameUnfollow(gameFollow);


        Mockito.verify(userService).unsubscribeFromGame(userId, gameId);
        Mockito.verify(gameService).getByIdWithLoadedUsers(gameId);
        Mockito.verify(gameService).setFollowGameOption(gameId, false);
    }

    @Test
    void testListenGameUnfollow_whenRemainFollower_shouldUnsubscribeFromUserAndNotCallGameService() {
        long userId = 1L;
        long gameId = 101L;
        GameFollowEvent event = new GameFollowEvent(userId, gameId);
        ConsumerRecord<String, GameFollowEvent> gameFollow = new ConsumerRecord<>("topic", 0, 0, "key", event);
        Game game = Game.builder().isFollowed(true).userList(Collections.singletonList(new BasicUser())).build();

        Mockito.when(gameService.getByIdWithLoadedUsers(gameId)).thenReturn(game);


        gameRequestListener.listenGameUnfollow(gameFollow);


        Mockito.verify(userService).unsubscribeFromGame(userId, gameId);
        Mockito.verify(gameService).getByIdWithLoadedUsers(gameId);
        Mockito.verifyNoMoreInteractions(gameService);
    }

    @Test
    void testListenGameRemove_whenSuccess_shouldCallRemoveGameMethod() {
        long gameId = 101L;
        ConsumerRecord<String, Long> gameFollow = new ConsumerRecord<>("topic", 0, 0, "key", gameId);


        gameRequestListener.listenGameRemove(gameFollow);


        Mockito.verify(gameService).removeGame(gameId);
    }

    @Test
    void testListenGameInStoreRemove_whenSuccess_shouldCallRemoveGameInStoreMethod() {
        long gameId = 102L;
        ConsumerRecord<String, Long> gameFollow = new ConsumerRecord<>("topic", 0, 0, "key", gameId);


        gameRequestListener.listenGameInStoreRemove(gameFollow);


        Mockito.verify(gameInShopService).removeGameInStore(gameId);
    }

    @Test
    void testListenAddGameInStore_whenSuccess_shouldCallAddGameInStoreMethod() {
        long gameId = 102L;
        String url = "url";
        AddGameInStoreDto dto = AddGameInStoreDto.builder().gameId(gameId).url(url).build();
        ConsumerRecord<String, AddGameInStoreDto> gameFollow = new ConsumerRecord<>("topic", 0, 0, "key", dto);


        gameRequestListener.listenAddGameInStore(gameFollow);


        Mockito.verify(gameInShopService).addGameInStore(gameId, url);
    }
}
