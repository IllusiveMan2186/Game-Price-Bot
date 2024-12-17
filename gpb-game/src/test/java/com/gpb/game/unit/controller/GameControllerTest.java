package com.gpb.game.unit.controller;

import com.gpb.game.bean.game.Game;
import com.gpb.game.bean.game.GameInfoDto;
import com.gpb.game.bean.game.GameListPageDto;
import com.gpb.game.bean.game.Genre;
import com.gpb.game.bean.game.ProductType;
import com.gpb.game.bean.user.BasicUser;
import com.gpb.game.controller.GameController;
import com.gpb.game.exception.PriceRangeException;
import com.gpb.game.exception.SortParamException;
import com.gpb.game.service.GameService;
import com.gpb.game.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private UserService userService;

    @InjectMocks
    private GameController gameController;

    @Test
    void testGetGameById_whenSuccess_shouldReturnGameInfo() {
        long gameId = 1L;
        long userId = 101L;
        GameInfoDto mockGameInfo = new GameInfoDto();
        when(gameService.getDtoById(gameId)).thenReturn(mockGameInfo);

        BasicUser user = new BasicUser();
        user.setGameList(List.of(Game.builder().id(gameId).build()));
        when(userService.getUserById(userId)).thenReturn(user);


        GameInfoDto result = gameController.getGameById(gameId, userId);


        assertNotNull(result);
        assertEquals(mockGameInfo, result);
        verify(gameService, times(1)).getDtoById(gameId);
    }

    @Test
    void testGetGameByName_whenSuccess_shouldReturnGameListPageDto() {
        String name = "GameName";
        int pageSize = 10;
        int pageNum = 1;
        String sortBy = "gamesInShop.price-ASC";
        GameListPageDto mockPage = new GameListPageDto();
        when(gameService.getByName(name, pageSize, pageNum, Sort.by(Sort.Direction.ASC, "gamesInShop.price")))
                .thenReturn(mockPage);


        GameListPageDto result = gameController.getGameByName(name, pageSize, pageNum, sortBy, null);


        assertNotNull(result);
        verify(gameService, times(1)).getByName(name, pageSize, pageNum, Sort.by(Sort.Direction.ASC, "gamesInShop.price"));
    }

    @Test
    void testGetGameByUrl_whenSuccess_shouldReturnGameInfo() {
        String url = "http://example.com/game";
        GameInfoDto mockGameInfo = new GameInfoDto();
        when(gameService.getByUrl(url)).thenReturn(mockGameInfo);


        GameInfoDto result = gameController.getGameByUrl(url);


        assertNotNull(result);
        verify(gameService, times(1)).getByUrl(url);
    }

    @Test
    void testGetGamesForGenre_whenInvalidPriceRange_shouldThrowPriceRangeException() {
        BigDecimal minPrice = BigDecimal.valueOf(50);
        BigDecimal maxPrice = BigDecimal.valueOf(40);


        assertThrows(PriceRangeException.class, () ->
                gameController.getGamesForGenre(Collections.emptyList(), Collections.emptyList(), 10,
                        1, minPrice, maxPrice, "gamesInShop.price-ASC", null));
    }

    @Test
    void testGetGamesForGenre_whenSuccess_shouldReturnGameListPageDto() {
        List<Genre> genres = Arrays.asList(Genre.ACTION, Genre.ADVENTURES);
        List<ProductType> types = Collections.singletonList(ProductType.ADDITION);
        int pageSize = 10;
        int pageNum = 1;
        BigDecimal minPrice = BigDecimal.valueOf(20);
        BigDecimal maxPrice = BigDecimal.valueOf(60);
        String sortBy = "gamesInShop.price-DESC";
        GameListPageDto mockPage = new GameListPageDto();
        when(gameService.getByGenre(genres, types, pageSize, pageNum, minPrice, maxPrice, Sort.by(Sort.Direction.DESC, "gamesInShop.price")))
                .thenReturn(mockPage);


        GameListPageDto result = gameController.getGamesForGenre(genres, types, pageSize, pageNum, minPrice, maxPrice, sortBy, null);


        assertNotNull(result);
        verify(gameService, times(1)).getByGenre(genres, types, pageSize, pageNum, minPrice, maxPrice, Sort.by(Sort.Direction.DESC, "gamesInShop.price"));
    }

    @Test
    void testGetGamesOfUser_whenSuccess_shouldReturnGameListPageDto() {
        int pageSize = 10;
        int pageNum = 1;
        String sortBy = "name-ASC";
        long userId = 102L;
        GameListPageDto mockPage = new GameListPageDto();
        when(gameService.getUserGames(userId, pageSize, pageNum, Sort.by(Sort.Direction.ASC, "name")))
                .thenReturn(mockPage);


        GameListPageDto result = gameController.getGamesOfUser(pageSize, pageNum, sortBy, userId);


        assertNotNull(result);
        verify(gameService, times(1)).getUserGames(userId, pageSize, pageNum, Sort.by(Sort.Direction.ASC, "name"));
    }

    @Test
    void testGetGameByName_whenSuccess_shouldThrowSortParamExceptionForInvalidSortBy() {
        String name = "GameName";
        int pageSize = 10;
        int pageNum = 1;
        String invalidSortBy = "invalid-param";


        assertThrows(SortParamException.class, () ->
                gameController.getGameByName(name, pageSize, pageNum, invalidSortBy, null));
    }

    @Test
    void testGetGameByName_whenSuccess_shouldReturnGameListPageDtoForValidSortBy() {
        String name = "GameName";
        int pageSize = 10;
        int pageNum = 1;
        String validSortBy = "gamesInShop.price-ASC";
        GameListPageDto mockPage = new GameListPageDto();
        when(gameService.getByName(name, pageSize, pageNum, Sort.by(Sort.Direction.ASC, "gamesInShop.price")))
                .thenReturn(mockPage);


        GameListPageDto result = gameController.getGameByName(name, pageSize, pageNum, validSortBy, null);


        assertNotNull(result);
        verify(gameService, times(1)).getByName(name, pageSize, pageNum, Sort.by(Sort.Direction.ASC, "gamesInShop.price"));
    }

}
