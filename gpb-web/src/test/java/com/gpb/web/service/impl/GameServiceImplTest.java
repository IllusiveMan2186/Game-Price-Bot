package com.gpb.web.service.impl;

import com.gpb.web.bean.Game;
import com.gpb.web.bean.Genre;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.exception.UrlAlreadyExistException;
import com.gpb.web.repository.GameRepository;
import com.gpb.web.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameServiceImplTest {

    GameRepository repository = mock(GameRepository.class);

    GameService gameService = new GameServiceImpl(repository);

    private final Game game = new Game();

    @Test
    void getGameByIdSuccessfullyShouldReturnGame() {
        int id = 1;
        when(repository.findById(id)).thenReturn(game);

        Game result = gameService.getById(id);

        assertEquals(game, result);
    }

    @Test
    void getGameByIdThatNotFoundShouldThrowException() {
        int id = 1;
        when(repository.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            gameService.getById(id);
        }, "Game with id '1' not found");
    }

    @Test
    void getGameByNameSuccessfullyShouldReturnGame() {
        String name = "name";
        when(repository.findByName(name)).thenReturn(game);

        Game result = gameService.getByName(name);

        assertEquals(game, result);
    }

    @Test
    void getGameByNameThatNotFoundShouldThrowException() {
        String name = "name";
        when(repository.findByName(name)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            gameService.getByName(name);
        }, "Game with name 'name' not found");
    }

    @Test
    void getGameByUrlSuccessfullyShouldReturnGame() {
        String url = "url";
        when(repository.findByUrl(url)).thenReturn(game);

        Game result = gameService.getByUrl(url);

        assertEquals(game, result, "Game with url 'url' not found");
    }

    @Test
    void getGameByUrlThatNotFoundShouldThrowException() {
        String url = "url";
        when(repository.findByUrl(url)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            gameService.getByUrl(url);
        });
    }

    @Test
    void findByGenreSuccessfullyShouldReturnGameList() {
        Genre genre = Genre.STRATEGY;
        int pageSize = 2;
        int pageNum = 2;
        List<Game> gameList = Collections.singletonList(game);
        when(repository.findByGenre(genre, PageRequest.of(pageNum - 1, pageSize)))
                .thenReturn(gameList);

        List<Game> result = gameService.getByGenre(genre, pageSize, pageNum);

        assertEquals(gameList, result);
    }

    @Test
    void createUserSuccessfullyShouldSaveAndReturnUser() {
        String url = "url";
        game.setUrl(url);
        when(repository.findByUrl(url)).thenReturn(null);
        when(repository.save(game)).thenReturn(game);

        Game result = gameService.create(game);

        assertEquals(game, result);
    }

    @Test
    void createUserWithRegisteredEmailShouldThrowException() {
        String url = "url";
        game.setUrl(url);
        when(repository.findByUrl(url)).thenReturn(game);

        assertThrows(UrlAlreadyExistException.class, () -> {
            gameService.create(game);
        }, "Game with this url already exist");
    }
}