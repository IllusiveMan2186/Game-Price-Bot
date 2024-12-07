package com.gpb.stores.unit.controller;

import com.gpb.stores.bean.game.GameInfoDto;
import com.gpb.stores.bean.game.GameListPageDto;
import com.gpb.stores.bean.game.Genre;
import com.gpb.stores.bean.game.ProductType;
import com.gpb.stores.controller.GameController;
import com.gpb.stores.exception.PriceRangeException;
import com.gpb.stores.exception.SortParamException;
import com.gpb.stores.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameControllerTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getGameById_ShouldReturnGameInfo() {
        // Arrange
        long gameId = 1L;
        long userId = 101L;
        GameInfoDto mockGameInfo = new GameInfoDto();
        when(gameService.getById(gameId, userId)).thenReturn(mockGameInfo);

        // Act
        GameInfoDto result = gameController.getGameById(gameId, userId);

        // Assert
        assertNotNull(result);
        verify(gameService, times(1)).getById(gameId, userId);
    }

    @Test
    void getGameByName_ShouldReturnGameListPageDto() {
        // Arrange
        String name = "GameName";
        int pageSize = 10;
        int pageNum = 1;
        String sortBy = "gamesInShop.price-ASC";
        GameListPageDto mockPage = new GameListPageDto();
        when(gameService.getByName(name, pageSize, pageNum, Sort.by(Sort.Direction.ASC, "gamesInShop.price")))
                .thenReturn(mockPage);

        // Act
        GameListPageDto result = gameController.getGameByName(name, pageSize, pageNum, sortBy);

        // Assert
        assertNotNull(result);
        verify(gameService, times(1)).getByName(name, pageSize, pageNum, Sort.by(Sort.Direction.ASC, "gamesInShop.price"));
    }

    @Test
    void getGameByUrl_ShouldReturnGameInfo() {
        // Arrange
        String url = "http://example.com/game";
        GameInfoDto mockGameInfo = new GameInfoDto();
        when(gameService.getByUrl(url)).thenReturn(mockGameInfo);

        // Act
        GameInfoDto result = gameController.getGameByUrl(url);

        // Assert
        assertNotNull(result);
        verify(gameService, times(1)).getByUrl(url);
    }

    @Test
    void getGamesForGenre_ShouldThrowPriceRangeExceptionForInvalidRange() {
        // Arrange
        BigDecimal minPrice = BigDecimal.valueOf(50);
        BigDecimal maxPrice = BigDecimal.valueOf(40);

        // Act & Assert
        assertThrows(PriceRangeException.class, () ->
                gameController.getGamesForGenre(Collections.emptyList(), Collections.emptyList(), 10, 1, minPrice, maxPrice, "gamesInShop.price-ASC"));
    }

    @Test
    void getGamesForGenre_ShouldReturnGameListPageDto() {
        // Arrange
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

        // Act
        GameListPageDto result = gameController.getGamesForGenre(genres, types, pageSize, pageNum, minPrice, maxPrice, sortBy);

        // Assert
        assertNotNull(result);
        verify(gameService, times(1)).getByGenre(genres, types, pageSize, pageNum, minPrice, maxPrice, Sort.by(Sort.Direction.DESC, "gamesInShop.price"));
    }

    @Test
    void getGamesOfUser_ShouldReturnGameListPageDto() {
        // Arrange
        int pageSize = 10;
        int pageNum = 1;
        String sortBy = "gamesInShop.name-ASC";
        long userId = 102L;
        GameListPageDto mockPage = new GameListPageDto();
        when(gameService.getUserGames(userId, pageSize, pageNum, Sort.by(Sort.Direction.ASC, "gamesInShop.name")))
                .thenReturn(mockPage);

        // Act
        GameListPageDto result = gameController.getGamesOfUser(pageSize, pageNum, sortBy, userId);

        // Assert
        assertNotNull(result);
        verify(gameService, times(1)).getUserGames(userId, pageSize, pageNum, Sort.by(Sort.Direction.ASC, "gamesInShop.name"));
    }

    @Test
    void getGameByName_ShouldThrowSortParamExceptionForInvalidSortBy() {
        // Arrange
        String name = "GameName";
        int pageSize = 10;
        int pageNum = 1;
        String invalidSortBy = "invalid-param";

        // Act & Assert
        assertThrows(SortParamException.class, () ->
                gameController.getGameByName(name, pageSize, pageNum, invalidSortBy));
    }

    @Test
    void getGameByName_ShouldReturnGameListPageDtoForValidSortBy() {
        // Arrange
        String name = "GameName";
        int pageSize = 10;
        int pageNum = 1;
        String validSortBy = "gamesInShop.price-ASC";
        GameListPageDto mockPage = new GameListPageDto();
        when(gameService.getByName(name, pageSize, pageNum, Sort.by(Sort.Direction.ASC, "gamesInShop.price")))
                .thenReturn(mockPage);

        // Act
        GameListPageDto result = gameController.getGameByName(name, pageSize, pageNum, validSortBy);

        // Assert
        assertNotNull(result);
        verify(gameService, times(1)).getByName(name, pageSize, pageNum, Sort.by(Sort.Direction.ASC, "gamesInShop.price"));
    }

}
