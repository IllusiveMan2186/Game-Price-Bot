package com.gpb.web.service.impl;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameDto;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.GameInfoDto;
import com.gpb.web.bean.game.GameListPageDto;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.exception.GameAlreadyRegisteredException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.repository.GameInShopRepository;
import com.gpb.web.repository.GameRepository;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
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

    private final GameInShop gameInShop = GameInShop.builder()
            .price(new BigDecimal(1))
            .build();


    private final Game game = Game.builder().gamesInShop(Collections.singletonList(gameInShop)).build();


    @Test
    void getGameByIdSuccessfullyShouldReturnGame() {
        int id = 1;
        when(repository.findById(id)).thenReturn(game);
        GameInfoDto gameInfoDto = new GameInfoDto(game);

        GameInfoDto result = gameService.getById(id);

        assertEquals(gameInfoDto, result);
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
        GameInfoDto gameInfoDto = new GameInfoDto(game);

        GameInfoDto result = gameService.getByName(name);

        assertEquals(gameInfoDto, result);
    }

    @Test
    void getGameByNameThatNotRegisteredShouldFindGameFromStoresService() {
        String name = "name";
        when(repository.findByName(name)).thenReturn(null);
        when(gameStoresService.findGameByName(name)).thenReturn(game);
        game.setName(name);
        when(repository.findByName(name)).thenReturn(null);
        when(repository.save(game)).thenReturn(game);
        GameInfoDto gameInfoDto = new GameInfoDto(game);

        GameInfoDto result = gameService.getByName(name);

        assertEquals(gameInfoDto, result);
    }

    @Test
    void getGameByUrlSuccessfullyShouldReturnGame() {
        String url = "url";
        GameInShop gameInShop = GameInShop.builder().game(game).build();
        when(gameInShopRepository.findByUrl(url)).thenReturn(gameInShop);
        GameInfoDto gameInfoDto = new GameInfoDto(game);

        GameInfoDto result = gameService.getByUrl(url);

        assertEquals(gameInfoDto, result);
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
        GameInfoDto gameInfoDto = new GameInfoDto(game);

        GameInfoDto result = gameService.getByUrl(url);

        assertEquals(gameInfoDto, result);
    }

    @Test
    void findByGenreSuccessfullyShouldReturnGameList() {
        Genre genre = Genre.STRATEGIES;
        int pageSize = 2;
        int pageNum = 2;
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(GameDto::new).toList();
        when(repository.findByGenresIn(Collections.singletonList(genre), PageRequest.of(pageNum - 1, pageSize)))
                .thenReturn(gameList);
        when(repository.countByGenresIn(Collections.singletonList(genre))).thenReturn(1L);
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);

        GameListPageDto result = gameService.getByGenre(Collections.singletonList(genre), pageSize, pageNum);

        assertEquals(gameListPageDto, result);
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