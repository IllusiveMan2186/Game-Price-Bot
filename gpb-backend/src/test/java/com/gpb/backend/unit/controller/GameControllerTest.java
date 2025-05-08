package com.gpb.backend.unit.controller;

import com.gpb.backend.controller.GameController;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.GameService;
import com.gpb.common.entity.game.AddGameInStoreDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.exception.PriceRangeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @Test
    void testGetGameById_whenSuccess_shouldReturnGameInfo() {
        long gameId = 1L;
        UserDto user = new UserDto("username", "password", "token", "role", "ua");
        user.setId(123L);

        GameInfoDto gameInfo = new GameInfoDto();
        when(gameService.getById(gameId, user.getBasicUserId())).thenReturn(gameInfo);


        GameInfoDto result = gameController.getGameById(gameId, user);


        assertNotNull(result);
        verify(gameService).getById(gameId, user.getBasicUserId());
    }

    @Test
    void testGetGameByName_whenSuccess_shouldReturnGameList() {
        String name = "Test Game";
        int pageSize = 25;
        int pageNum = 1;
        String sortBy = "gamesInShop.discountPrice-ASC";

        GameListPageDto gameList = new GameListPageDto();
        when(gameService.getByName(name, pageSize, pageNum, sortBy)).thenReturn(gameList);


        GameListPageDto result = gameController.getGameByName(name, pageSize, pageNum, sortBy);


        assertNotNull(result);
        verify(gameService).getByName(name, pageSize, pageNum, sortBy);
    }

    @Test
    void testGetGamesForGenre_whenInvalidPriceRange_shouldThrowException() {
        BigDecimal minPrice = BigDecimal.valueOf(100);
        BigDecimal maxPrice = BigDecimal.valueOf(50);


        assertThrows(PriceRangeException.class, () ->
                gameController.getGamesForGenre(List.of(), List.of(), 25, 1, minPrice, maxPrice, "gamesInShop.discountPrice-ASC"));
    }

    @Test
    void testGetGamesForGenre_whenSuccess_shouldReturnGameList() {

        List<Genre> genres = List.of(Genre.ACTION);
        List<ProductType> types = List.of(ProductType.GAME);
        int pageSize = 25;
        int pageNum = 1;
        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal maxPrice = BigDecimal.valueOf(100);
        String sortBy = "gamesInShop.discountPrice-ASC";

        GameListPageDto gameList = new GameListPageDto();
        when(gameService.getByGenre(genres, types, pageSize, pageNum, minPrice, maxPrice,
                "gamesInShop.discountPrice-ASC")).thenReturn(gameList);


        GameListPageDto result = gameController.getGamesForGenre(genres, types, pageSize, pageNum, minPrice, maxPrice, sortBy);


        assertNotNull(result);
        verify(gameService).getByGenre(genres, types, pageSize, pageNum, minPrice, maxPrice,
                "gamesInShop.discountPrice-ASC");
    }

    @Test
    void testGetGameByUrl_whenSuccess_shouldReturnGameInfo() {
        String url = "http://test-game-url";
        GameInfoDto gameInfo = new GameInfoDto();
        when(gameService.getByUrl(url)).thenReturn(gameInfo);


        GameInfoDto result = gameController.getGameByUrl(url);


        assertNotNull(result);
        verify(gameService).getByUrl(url);
    }

    @Test
    void testAddGameInStore_whenSuccess_shouldReturnGameInfo() {
        AddGameInStoreDto addGameInStoreDto = new AddGameInStoreDto();

        gameController.addGameInStoreToCreatedGameByUrl(addGameInStoreDto);

        verify(gameService).addGameInStore(addGameInStoreDto);
    }

    @Test
    void testRemoveGameById_whenSuccess_shouldInvokeServiceMethod() {
        long gameId = 1L;


        gameController.removeGameById(gameId);


        verify(gameService).removeGame(gameId);
    }

    @Test
    void testRemoveGameInStoreById_whenSuccess_shouldInvokeServiceMethod() {
        long gameInStoreId = 1L;


        gameController.removeGameInStoreById(gameInStoreId);


        verify(gameService).removeGameInStore(gameInStoreId);
    }

    @Test
    void testGetGameImage_whenSuccess_shouldReturnImageBytes() {
        String gameName = "Test Game";
        byte[] imageBytes = new byte[]{1, 2, 3};
        when(gameService.getGameImage(gameName)).thenReturn(imageBytes);


        byte[] result = gameController.getGameImage(gameName);


        assertArrayEquals(imageBytes, result);
        verify(gameService).getGameImage(gameName);
    }

    @Test
    void testGetGamesOfUser_whenSuccess_shouldReturnGameList() {
        UserDto user = new UserDto("username", "password", "token", "role", "ua");
        user.setBasicUserId(123L);

        int pageSize = 25;
        int pageNum = 1;
        String sortBy = "gamesInShop.discountPrice-ASC";

        GameListPageDto gameList = new GameListPageDto();
        when(gameService.getUserGames(user.getBasicUserId(), pageSize, pageNum, sortBy)).thenReturn(gameList);


        GameListPageDto result = gameController.getGamesOfUser(pageSize, pageNum, sortBy, user);


        assertNotNull(result);
        verify(gameService).getUserGames(user.getBasicUserId(), pageSize, pageNum, sortBy);
    }
}
