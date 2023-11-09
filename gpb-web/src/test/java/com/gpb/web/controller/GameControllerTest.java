package com.gpb.web.controller;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameDto;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.GameInfoDto;
import com.gpb.web.bean.game.GameListPageDto;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.exception.PriceRangeException;
import com.gpb.web.exception.SortParamException;
import com.gpb.web.service.GameService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameControllerTest {

    GameService service = mock(GameService.class);

    private final GameController controller = new GameController(service);

    private final GameInShop gameInShop = GameInShop.builder()
            .price(new BigDecimal(2))
            .discountPrice(new BigDecimal(1))
            .build();

    private final Game game = Game.builder()
            .gamesInShop(Collections.singletonList(gameInShop))
            .build();

    @BeforeAll
    static void beforeAll() {

    }

    @Test
    void getGameByIdSuccessfullyShouldReturnGame() {
        int id = 1;
        int userId = 1;
        WebUser user = new WebUser("email", "password", false, 0, null);
        user.setId(userId);
        GameInfoDto gameInfoDto = new GameInfoDto(game);
        when(service.getById(id, userId)).thenReturn(gameInfoDto);

        GameInfoDto result = controller.getGamerById(id, new UserDto(user));

        assertEquals(gameInfoDto, result);
    }

    @Test
    void getGameByGameNameSuccessfullyShouldReturnGame() {
        String name = "name";
        int pageSize = 2;
        int pageNum = 2;
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(GameDto::new).toList();
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(service.getByName(name, pageNum, pageSize, sort)).thenReturn(gameListPageDto);

        GameListPageDto result = controller.getGameByName(name, pageNum, pageSize, "name-ASC");

        assertEquals(gameListPageDto, result);
    }

    @Test
    void getGameByEmailSuccessfullyShouldReturnGame() {
        String url = "email";
        GameInfoDto gameInfoDto = new GameInfoDto(game);
        when(service.getByUrl(url)).thenReturn(gameInfoDto);

        GameInfoDto result = controller.getGameByUrl(url);

        assertEquals(gameInfoDto, result);
    }

    @Test
    void findByGenreSuccessfullyShouldReturnGameList() {
        List<Genre> genre = Collections.singletonList(Genre.STRATEGIES);
        int pageSize = 2;
        int pageNum = 2;
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(GameDto::new).toList();
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(service.getByGenre(genre, pageNum, pageSize, new BigDecimal(0), new BigDecimal(1), sort))
                .thenReturn(new GameListPageDto(1, gameDtoList));

        GameListPageDto result = controller
                .getGamesForGenre(genre, pageSize, pageNum, new BigDecimal(0), new BigDecimal(1), "name-ASC");

        assertEquals(gameListPageDto, result);
    }

    @Test
    void getGamesOfUserSuccessfullyShouldReturnGameList() {
        int userId = 1;
        int pageSize = 2;
        int pageNum = 2;
        WebUser user = new WebUser("email", "password", false, 0, null);
        user.setId(userId);
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(GameDto::new).toList();
        GameListPageDto expected = new GameListPageDto(1, gameDtoList);
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(service.getUserGames(userId, pageNum, pageSize, sort))
                .thenReturn(expected);

        GameListPageDto result = controller.getGamesOfUser(pageSize, pageNum, "name-ASC", new UserDto(user));

        assertEquals(expected, result);
    }

    @Test
    void getGameByInvalidPriceRangeUnsuccessfullyShouldThrowException() {
        assertThrows(PriceRangeException.class, () -> controller
                .getGamesForGenre(new ArrayList<>(), 1, 1, new BigDecimal(10), new BigDecimal(1), "name-ASC"));
    }

    @Test
    void getGameByInvalidSortByUnsuccessfullyShouldThrowException() {
        assertThrows(SortParamException.class, () -> controller
                .getGamesForGenre(new ArrayList<>(), 1, 1, new BigDecimal(1), new BigDecimal(1), "name"));
    }
}