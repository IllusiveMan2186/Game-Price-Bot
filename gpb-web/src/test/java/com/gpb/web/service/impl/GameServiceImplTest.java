package com.gpb.web.service.impl;

import com.gpb.web.bean.Game;
import com.gpb.web.bean.GameInShop;
import com.gpb.web.bean.Genre;
import com.gpb.web.exception.GameAlreadyRegisteredException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.repository.GameInShopRepository;
import com.gpb.web.repository.GameRepository;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
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
    GameInShopRepository gameInShopRepository = mock(GameInShopRepository.class);

    GameStoresService gameStoresService = mock(GameStoresService.class);

    GameService gameService = new GameServiceImpl(repository, gameInShopRepository, gameStoresService);

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

        assertThrows(NotFoundException.class, () -> gameService.getById(id), "Game with id '1' not found");
    }

    @Test
    void getGameByNameSuccessfullyShouldReturnGame() {
        String name = "name";
        when(repository.findByName(name)).thenReturn(game);

        Game result = gameService.getByName(name);

        assertEquals(game, result);
    }

    @Test
    void getGameByNameThatNotRegisteredShouldFindGameFromStoresService() {
        String name = "name";
        when(repository.findByName(name)).thenReturn(null);
        when(gameStoresService.findGameByName(name)).thenReturn(game);
        game.setName(name);
        when(repository.findByName(name)).thenReturn(null);
        when(repository.save(game)).thenReturn(game);

        Game result = gameService.getByName(name);

        assertEquals(game, result);
    }

    @Test
    void getGameByUrlSuccessfullyShouldReturnGame() {
        String url = "url";
        GameInShop gameInShop = GameInShop.builder().game(game).build();
        when(gameInShopRepository.findByUrl(url)).thenReturn(gameInShop);

        Game result = gameService.getByUrl(url);

        assertEquals(game, result);
    }

    @Test
    void getGameByUrlThatNotRegisteredShouldFindGameFromStoresService() {
        String url = "url";
        when(gameInShopRepository.findByUrl(url)).thenReturn(null);
        when(gameStoresService.findGameByUrl(url)).thenReturn(game);
        String name = "name";
        game.setName(name);
        when(repository.findByName(name)).thenReturn(null);
        when(repository.save(game)).thenReturn(game);

        Game result = gameService.getByUrl(url);

        assertEquals(game, result);
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
        String name = "name";
        game.setName(name);
        when(repository.findByName(name)).thenReturn(null);
        when(repository.save(game)).thenReturn(game);

        Game result = gameService.create(game);

        assertEquals(game, result);
    }

    @Test
    void createUserWithRegisteredEmailShouldThrowException() {
        String name = "url";
        game.setName(name);
        when(repository.findByName(name)).thenReturn(game);

        assertThrows(GameAlreadyRegisteredException.class, () -> gameService.create(game), "Game with this url already exist");
    }
}