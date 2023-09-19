package com.gpb.web.controller;

import com.gpb.web.bean.Game;
import com.gpb.web.bean.Genre;
import com.gpb.web.service.GameService;
import org.junit.jupiter.api.Test;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameControllerTest {

    GameService service = mock(GameService.class);

    private GameController controller = new GameController(service);

    private Game game = new Game();

    @Test
    void getGameByIdSuccessfullyShouldReturnGame() {
        int id = 1;
        when(service.getById(id)).thenReturn(game);

        Game result = controller.getGamerById(id);

        assertEquals(game, result);
    }

    @Test
    void getGameByGamenameSuccessfullyShouldReturnGame() {
        String name = "name";
        when(service.getByName(name)).thenReturn(game);

        Game result = controller.getGameByName(name);

        assertEquals(game, result);
    }

    @Test
    void getGameByEmailSuccessfullyShouldReturnGame() {
        String url = "email";
        when(service.getByUrl(url)).thenReturn(game);

        Game result = controller.getGameByUrl(url);

        assertEquals(game, result);
    }

    @Test
    void findByGenreSuccessfullyShouldReturnGameList() {
        Genre genre = Genre.STRATEGY;
        int pageSize = 2;
        int pageNum = 2;
        List<Game> gameList = Collections.singletonList(game);
        when(service.getByGenre(genre, pageNum , pageSize))
                .thenReturn(gameList);

        List<Game> result = controller.getGamesForGenre(genre, pageSize, pageNum);

        assertEquals(gameList, result);
    }
}