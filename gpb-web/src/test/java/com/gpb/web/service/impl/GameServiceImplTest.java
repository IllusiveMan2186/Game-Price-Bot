package com.gpb.web.service.impl;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameDto;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.GameInfoDto;
import com.gpb.web.bean.game.GameListPageDto;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.bean.user.BasicUser;
import com.gpb.web.configuration.MapperConfig;
import com.gpb.web.exception.GameAlreadyRegisteredException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.repository.GameInShopRepository;
import com.gpb.web.repository.GameRepository;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    GameService gameService = new GameServiceImpl(repository, gameInShopRepository, gameStoresService, modelMapper);

    private final GameInShop gameInShop = GameInShop.builder()
            .price(new BigDecimal(2))
            .discountPrice(new BigDecimal(1))
            .build();


    private final Game game = Game.builder().gamesInShop(Collections.singletonList(gameInShop)).build();


    @Test
    void getGameByIdSuccessfullyShouldReturnGame() {
        int id = 1;
        int userId = 1;
        game.setUserList(new ArrayList<>());
        when(repository.findById(id)).thenReturn(game);
        GameInfoDto gameInfoDto = modelMapper.map(game, GameInfoDto.class);

        GameInfoDto result = gameService.getById(id, userId);

        assertEquals(gameInfoDto, result);
    }

    @Test
    void getGameByIdThatNotFoundShouldThrowException() {
        int id = 1;
        int userId = 1;
        when(repository.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> gameService.getById(id, userId), "Game with id '1' not found");
    }

    @Test
    void getGameByNameSuccessfullyShouldReturnGame() {
        String name = "name";
        int pageSize = 2;
        int pageNum = 2;
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(game -> modelMapper.map(game, GameDto.class)).toList();
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(repository.findByNameContainingIgnoreCase(name, PageRequest.of(pageNum - 1, pageSize, sort))).thenReturn(gameList);
        when(repository.countAllByNameContainingIgnoreCase(name)).thenReturn(1L);
        GameListPageDto result = gameService.getByName(name, pageSize, pageNum, sort);

        assertEquals(gameListPageDto, result);
    }

    @Test
    void getGameByNameThatNotRegisteredShouldFindGameFromStoresService() {
        String name = "name";
        int pageSize = 2;
        int pageNum = 2;
        game.setName(name);
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(game -> modelMapper.map(game, GameDto.class)).toList();
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(repository.findByNameContainingIgnoreCase(name, PageRequest.of(pageNum - 1, pageSize, sort)))
                .thenReturn(new ArrayList<>());
        when(gameStoresService.findGameByName(name)).thenReturn(gameList);
        when(repository.findByName(name)).thenReturn(null);
        when(repository.saveAll(gameList)).thenReturn(gameList);
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);

        GameListPageDto result = gameService.getByName(name, pageSize, pageNum, sort);

        assertEquals(gameListPageDto, result);
    }

    @Test
    void getGameByUrlSuccessfullyShouldReturnGame() {
        String url = "url";
        GameInShop gameInShop = GameInShop.builder().game(game).build();
        when(gameInShopRepository.findByUrl(url)).thenReturn(gameInShop);
        GameInfoDto gameInfoDto = modelMapper.map(game, GameInfoDto.class);

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
        GameInfoDto gameInfoDto = modelMapper.map(game, GameInfoDto.class);

        GameInfoDto result = gameService.getByUrl(url);

        assertEquals(gameInfoDto, result);
    }

    @Test
    void findByGenreSuccessfullyShouldReturnGameList() {
        Genre genre = Genre.STRATEGIES;
        int pageSize = 2;
        int pageNum = 2;
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(game -> modelMapper.map(game, GameDto.class)).toList();
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(repository.findByGenresInAndGamesInShop_DiscountPriceBetween(Collections.singletonList(genre),
                PageRequest.of(pageNum - 1, pageSize, sort), new BigDecimal(0), new BigDecimal(1)))
                .thenReturn(gameList);
        when(repository.countByGenresIn(Collections.singletonList(genre))).thenReturn(1L);
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);

        GameListPageDto result = gameService.getByGenre(Collections.singletonList(genre), pageSize, pageNum,
                new BigDecimal(0), new BigDecimal(1), sort);

        assertEquals(gameListPageDto, result);
    }

    @Test
    void findUserGamesSuccessfullyShouldReturnGameList() {
        int pageSize = 1;
        int pageNum = 1;
        int userId = 1;
        BasicUser user = new BasicUser();
        user.setId(userId);
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(game -> modelMapper.map(game, GameDto.class)).toList();
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(repository.findByUserList(user, PageRequest.of(pageNum - 1, pageSize, sort))).thenReturn(gameList);
        when(repository.countAllByUserList(user)).thenReturn(1L);
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);

        GameListPageDto result = gameService.getUserGames(userId, pageSize, pageNum, sort);

        assertEquals(gameListPageDto, result);
    }

    @Test
    void createGameSuccessfullyShouldSaveAndReturnUser() {
        String name = "name";
        game.setName(name);
        when(repository.findByName(name)).thenReturn(null);
        when(repository.save(game)).thenReturn(game);

        Game result = gameService.create(game);

        assertEquals(game, result);
    }

    @Test
    void createGameWithRegisteredEmailShouldThrowException() {
        String name = "url";
        game.setName(name);
        when(repository.findByName(name)).thenReturn(game);

        assertThrows(GameAlreadyRegisteredException.class, () -> gameService.create(game), "Game with this url already exist");
    }
}