package com.gpb.stores.service.impl;

import com.gpb.stores.bean.game.Game;
import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.bean.user.WebUser;
import com.gpb.stores.exception.NotFoundException;
import com.gpb.stores.repository.GameInShopRepository;
import com.gpb.stores.repository.GameRepository;
import com.gpb.stores.service.GameService;
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
    void testGetByIdGame_whenSuccessfully_shouldReturnGame() {
        long gameId = 123L;
        Game expectedGame = new Game();
        when(repository.findById(gameId)).thenReturn(expectedGame);

        Game result = gameService.getById(gameId);

        assertEquals(expectedGame, result);
        verify(repository, times(1)).findById(gameId);
    }

    @Test
    void testGetByIdGame_whenNotExist_ShouldThrowException() {
        long gameId = 123L;
        when(repository.findById(gameId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> gameService.getById(gameId), "app.game.error.id.not.found");
        verify(repository, times(1)).findById(gameId);
    }

    @Test
    void testGetSubscribedGames_whenSuccessfully_thenShouldGetGames() {
        List<GameInShop> games = new ArrayList<>();
        when(gameInShopRepository.findSubscribedGames()).thenReturn(games);

        List<GameInShop> result = gameService.getSubscribedGames();

        assertEquals(games, result);
    }

    @Test
    void testChangeInfo_whenSuccessfully_thenSaveChanges() {
        List<GameInShop> changedGames = new ArrayList<>();

        gameService.changeInfo(changedGames);

        verify(gameInShopRepository).saveAll(changedGames);
    }

    @Test
    void testGetUsersChangedGames_whenSuccessfully_thenShouldGetGames() {
        List<GameInShop> changedGames = new ArrayList<>();
        GameInShop gameInShop1 = GameInShop.builder().id(0).build();
        GameInShop gameInShop2 = GameInShop.builder().id(1).build();
        WebUser user = new WebUser();
        user.setId(1);
        when(gameInShopRepository.findSubscribedGames(user.getId(), List.of(0L, 1L))).thenReturn(changedGames);

        List<GameInShop> result = gameService.getUsersChangedGames(user, List.of(gameInShop1, gameInShop2));

        assertEquals(changedGames, result);
    }

    @Test
    public void testAddGames_whenSuccessfully_thenShouldGetGameIds() {
        List<Game> gamesToAdd = List.of(new Game());
        when(repository.saveAll(gamesToAdd)).thenReturn(gamesToAdd);

        List<Long> result = gameService.addGames(gamesToAdd);

        verify(repository).saveAll(gamesToAdd);
        List<Long> expectedIds = gamesToAdd.stream()
                .map(Game::getId)
                .toList();
        assertEquals(expectedIds, result);
    }
}