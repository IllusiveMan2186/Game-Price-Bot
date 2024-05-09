package com.gpb.telegram.service.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.GameInShop;
import com.gpb.telegram.exception.NotFoundException;
import com.gpb.telegram.repository.GameRepository;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.service.GameStoresService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameServiceImplTest {

    GameRepository repository = mock(GameRepository.class);
    GameStoresService gameStoresService = mock(GameStoresService.class);

    GameService gameService = new GameServiceImpl(repository, gameStoresService);

    private final GameInShop gameInShop = GameInShop.builder()
            .price(new BigDecimal(2))
            .discountPrice(new BigDecimal(1))
            .build();


    private final Game game = Game.builder().gamesInShop(Collections.singleton(gameInShop)).build();

    @Test
    void getGameByIdSuccessfullyShouldReturnGame() {
        long id = 1;
        game.setUserList(new ArrayList<>());
        when(repository.findById(id)).thenReturn(Optional.of(game));

        Game result = gameService.getById(id);

        assertEquals(game, result);
    }

    @Test
    void getGameByIdThatNotFoundShouldThrowException() {
        long id = 1;
        when(repository.findById(id)).thenReturn(null);
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gameService.getById(id));
    }

    @Test
    void testGetGameByName_whenGameInRepository_shouldReturnGameList() {
        String name = "name";
        int pageSize = 2;
        int pageNum = 2;
        List<Game> gameList = Collections.singletonList(game);
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(repository.findByNameContainingIgnoreCase(name, PageRequest.of(pageNum - 1, pageSize, sort)))
                .thenReturn(gameList);


        List<Game> result = gameService.getByName(name, pageNum);


        assertEquals(gameList, result);
    }

    @Test
    void testGetGameByName_whenGameNotFoundInRepository_shouldReturnGameListFromStoreService() {
        String name = "name";
        int pageSize = 2;
        int pageNum = 2;
        List<Game> gameList = Collections.singletonList(game);
        List<Long> gameIds = Collections.singletonList(game.getId());
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
        when(repository.findByNameContainingIgnoreCase(name, pageRequest)).thenReturn(new ArrayList<>());
        when(gameStoresService.findGameByName(name)).thenReturn(gameIds);
        when(repository.findByIdIn(gameIds, pageRequest)).thenReturn(gameList);


        List<Game> result = gameService.getByName(name, pageNum);


        assertEquals(gameList, result);
    }

    @Test
    void testGetGameAmountByName_shouldReturnName() {
        String name = "name";
        long pageNum = 2;
        when(repository.countAllByNameContainingIgnoreCase(name)).thenReturn(pageNum);


        long result = gameService.getGameAmountByName(name);


        assertEquals(pageNum, result);
    }

    @Test
    void testIsSubscribed_shouldReturnResult() {
        when(repository.existsByIdAndUserList_Id(1,2)).thenReturn(true);


        boolean result = gameService.isSubscribed(1, 2);


        assertTrue(result);
    }
}