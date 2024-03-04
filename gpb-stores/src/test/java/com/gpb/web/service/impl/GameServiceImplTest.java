package com.gpb.web.service.impl;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.repository.GameInShopRepository;
import com.gpb.web.repository.GameRepository;
import com.gpb.web.service.GameService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameServiceImplTest {

    GameRepository repository = mock(GameRepository.class);
    GameInShopRepository gameInShopRepository = mock(GameInShopRepository.class);


    GameService gameService = new GameServiceImpl(repository, gameInShopRepository);

    private final GameInShop gameInShop = GameInShop.builder()
            .price(new BigDecimal(2))
            .discountPrice(new BigDecimal(1))
            .build();


    private final Game game = Game.builder().gamesInShop(Collections.singleton(gameInShop)).build();

    @Test
    public void testGetByIdGameSuccessfullyShouldReturnGame() {
        long gameId = 123L;
        Game expectedGame = new Game();
        when(repository.findById(gameId)).thenReturn(expectedGame);

        Game result = gameService.getById(gameId);

        assertEquals(expectedGame, result);
        verify(repository, times(1)).findById(gameId);
    }

    @Test
    public void testGetByIdGameThatNotExistNullShouldThrowException() {
        long gameId = 123L;
        when(repository.findById(gameId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> gameService.getById(gameId),"app.game.error.id.not.found");
        verify(repository, times(1)).findById(gameId);
    }

    @Test
    void getSubscribedGamesSuccessfullyGameList() {
        List<GameInShop> games = new ArrayList<>();
        when(gameInShopRepository.findSubscribedGames()).thenReturn(games);

        List<GameInShop> result = gameService.getSubscribedGames();

        assertEquals(games, result);
    }

    @Test
    void changeInfoSuccessfullySaveChanges() {
        List<GameInShop> changedGames = new ArrayList<>();

        gameService.changeInfo(changedGames);

        verify(gameInShopRepository).saveAll(changedGames);
    }

    @Test
    void getUsersChangedGamesSuccessfullyShouldGetGames() {
        List<GameInShop> changedGames = new ArrayList<>();
        GameInShop gameInShop1 = GameInShop.builder().id(0).build();
        GameInShop gameInShop2 = GameInShop.builder().id(1).build();
        WebUser user = new WebUser();
        user.setId(1);
        when(gameInShopRepository.findSubscribedGames(user.getId(), List.of(0L, 1L))).thenReturn(changedGames);

        List<GameInShop> result = gameService.getUsersChangedGames(user, List.of(gameInShop1, gameInShop2));

        assertEquals(changedGames, result);
    }
}