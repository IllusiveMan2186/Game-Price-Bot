package com.gpb.game.unit.service.impl;

import com.gpb.game.bean.game.Game;
import com.gpb.game.bean.game.GameDto;
import com.gpb.game.bean.game.GameInShop;
import com.gpb.game.bean.game.GameInfoDto;
import com.gpb.game.bean.game.GameListPageDto;
import com.gpb.game.bean.game.Genre;
import com.gpb.game.bean.game.ProductType;
import com.gpb.game.bean.user.BasicUser;
import com.gpb.game.configuration.MapperConfig;
import com.gpb.game.exception.NotFoundException;
import com.gpb.game.repository.GameInShopRepository;
import com.gpb.game.repository.GameRepository;
import com.gpb.game.service.GameService;
import com.gpb.game.service.GameStoresService;
import com.gpb.game.service.impl.GameServiceImpl;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameServiceImplTest {

    GameRepository gameRepository = mock(GameRepository.class);
    GameInShopRepository gameInShopRepository = mock(GameInShopRepository.class);

    GameStoresService gameStoresService = mock(GameStoresService.class);

    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    GameService gameService = new GameServiceImpl(gameRepository, gameInShopRepository, gameStoresService, modelMapper);

    private final GameInShop gameInShop = GameInShop.builder()
            .price(new BigDecimal(2))
            .discountPrice(new BigDecimal(1))
            .build();


    private final Game game = Game.builder()
            .isFollowed(false)
            .gamesInShop(Collections.singleton(gameInShop))
            .build();

    @Test
    void testGetByIdGame_whenNotExist_ShouldThrowException() {
        long gameId = 123L;
        when(gameRepository.findById(gameId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> gameService.getById(gameId), "app.game.error.id.not.found");
        verify(gameRepository, times(1)).findById(gameId);
    }

    @Test
    void testGetSubscribedGames_whenSuccess_thenShouldGetGames() {
        List<GameInShop> games = new ArrayList<>();
        when(gameInShopRepository.findSubscribedGames()).thenReturn(games);

        List<GameInShop> result = gameService.getSubscribedGames();

        assertEquals(games, result);
    }

    @Test
    void testGetGameById_whenSuccess_shouldReturnGame() {
        int id = 1;
        game.setUserList(new ArrayList<>());
        when(gameRepository.findById(id)).thenReturn(game);
        GameInfoDto gameInfoDto = modelMapper.map(game, GameInfoDto.class);

        GameInfoDto result = gameService.getDtoById(id);

        assertEquals(gameInfoDto, result);
    }

    @Test
    void  testGetGameById_whenNotFound_shouldThrowException() {
        int id = 1;
        when(gameRepository.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> gameService.getDtoById(id), "Game with id '1' not found");
    }

    @Test
    void testGetGameByName_whenSuccess_shouldReturnGame() {
        String name = "name";
        int pageSize = 2;
        int pageNum = 2;
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(game -> modelMapper.map(game, GameDto.class)).toList();
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(gameRepository.findByNameContainingIgnoreCase(name, PageRequest.of(pageNum - 1, pageSize, sort))).thenReturn(gameList);
        when(gameRepository.countAllByNameContainingIgnoreCase(name)).thenReturn(1L);


        GameListPageDto result = gameService.getByName(name, pageSize, pageNum, sort);


        assertEquals(gameListPageDto, result);
    }

    @Test
    void testGetGameByName_whenThatNotRegistered_shouldFindGameFromStoresService() {
        String name = "name";
        int pageSize = 2;
        int pageNum = 2;
        game.setName(name);
        List<Game> gameList = Collections.singletonList(game);
        List<Long> gameIds = Collections.singletonList(1L);
        List<GameDto> gameDtoList = gameList.stream().map(game -> modelMapper.map(game, GameDto.class)).toList();
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(gameRepository.findByNameContainingIgnoreCase(name, PageRequest.of(pageNum - 1, pageSize, sort)))
                .thenReturn(new ArrayList<>());
        when(gameStoresService.findGameByName(name)).thenReturn(Collections.singletonList(game));
        when(gameRepository.findByName(name)).thenReturn(null);
        when(gameRepository.save(game)).thenReturn(game);
        when(gameRepository.findAllById(gameIds)).thenReturn(gameList);
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);


        GameListPageDto result = gameService.getByName(name, pageSize, pageNum, sort);


        assertEquals(gameListPageDto, result);
    }

    @Test
    void testGetGameByUrl_whenSuccess_shouldReturnGame() {
        String url = "url";
        GameInShop gameInShop = GameInShop.builder().game(game).build();
        when(gameInShopRepository.findByUrl(url)).thenReturn(gameInShop);
        GameInfoDto gameInfoDto = modelMapper.map(game, GameInfoDto.class);

        GameInfoDto result = gameService.getByUrl(url);

        assertEquals(gameInfoDto, result);
    }

    @Test
    void testGetGameByUrl_whenNotRegistered_shouldReturnGame() {
        String url = "url";
        long gameId = 1L;
        when(gameInShopRepository.findByUrl(url)).thenReturn(null);
        when(gameStoresService.findGameByUrl(url)).thenReturn(game);
        String name = "name";
        game.setName(name);
        when(gameRepository.findById(gameId)).thenReturn(game);
        GameInfoDto gameInfoDto = modelMapper.map(game, GameInfoDto.class);

        GameInfoDto result = gameService.getByUrl(url);

        assertEquals(gameInfoDto, result);
    }

    @Test
    void testFindByGenre_whenSuccess_shouldReturnGameList() {
        List<Genre> genre = Collections.singletonList(Genre.STRATEGIES);
        int pageSize = 2;
        List<ProductType> types = Collections.singletonList(ProductType.GAME);
        List<ProductType> typesToExclude = List.of(ProductType.ADDITION, ProductType.CURRENCY, ProductType.SUBSCRIPTION);
        int pageNum = 2;
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(game -> modelMapper.map(game, GameDto.class)).toList();
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(gameRepository.findByGenresInAndTypeInAndGamesInShop_DiscountPriceBetween(genre, types,
                PageRequest.of(pageNum - 1, pageSize, sort), new BigDecimal(0), new BigDecimal(1)))
                .thenReturn(gameList);
        when(gameRepository.countByGenresInAndTypeIn(genre, types)).thenReturn(1L);
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);

        GameListPageDto result = gameService.getByGenre(genre, typesToExclude, pageSize, pageNum,
                new BigDecimal(0), new BigDecimal(1), sort);

        assertEquals(gameListPageDto, result);
    }

    @Test
    void testFindUserGames_whenSuccess_shouldReturnGameList() {
        int pageSize = 1;
        int pageNum = 1;
        int userId = 1;
        BasicUser user = new BasicUser();
        user.setId(userId);
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(game -> modelMapper.map(game, GameDto.class)).toList();
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(gameRepository.findByUserList(user, PageRequest.of(pageNum - 1, pageSize, sort))).thenReturn(gameList);
        when(gameRepository.countAllByUserList(user)).thenReturn(1L);
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);

        GameListPageDto result = gameService.getUserGames(userId, pageSize, pageNum, sort);

        assertEquals(gameListPageDto, result);
    }

    @Test
    void testChangeInfo_whenSuccess_thenSaveChanges() {
        List<GameInShop> changedGames = new ArrayList<>();

        gameService.changeInfo(changedGames);

        verify(gameInShopRepository).saveAll(changedGames);
    }

    @Test
    void testGetUsersChangedGames_whenSuccess_thenShouldGetGames() {
        List<GameInShop> changedGames = new ArrayList<>();
        GameInShop gameInShop1 = GameInShop.builder().id(0).build();
        GameInShop gameInShop2 = GameInShop.builder().id(1).build();
        BasicUser user = new BasicUser();
        user.setId(1);
        when(gameInShopRepository.findSubscribedGames(user.getId(), List.of(0L, 1L))).thenReturn(changedGames);

        List<GameInShop> result = gameService.getUsersChangedGames(user, List.of(gameInShop1, gameInShop2));

        assertEquals(changedGames, result);
    }

    @Test
    void testRemoveGame_whenSuccess_shouldRemoveGame() {
        long gameId = 1L;

        gameService.removeGame(gameId);

        verify(gameRepository).deleteById(gameId);
    }

    @Test
    void testRemoveGameInStore_whenSuccess_shouldRemoveGameInStore() {
        long gameInStoreId = 1L;

        gameService.removeGameInStore(gameInStoreId);

        verify(gameInShopRepository).deleteById(gameInStoreId);
    }

    @Test
    void testSetFollowGameOption_whenSuccess_shouldSetFollowedOptionToTrue() {
        long gameId = 1L;
        when(gameRepository.findById(gameId)).thenReturn(game);
        Game expectedGame = Game.builder()
                .isFollowed(true)
                .gamesInShop(Collections.singleton(gameInShop))
                .build();

        gameService.setFollowGameOption(gameId, true);

        verify(gameRepository).save(expectedGame);
    }
}