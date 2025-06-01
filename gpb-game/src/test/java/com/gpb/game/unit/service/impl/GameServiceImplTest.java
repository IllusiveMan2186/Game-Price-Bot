package com.gpb.game.unit.service.impl;

import com.gpb.common.entity.game.GameDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.exception.NotFoundException;
import com.gpb.game.configuration.MapperConfig;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameRepositorySearchFilter;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.repository.GameRepository;
import com.gpb.game.repository.GameRepositoryCustom;
import com.gpb.game.service.GameService;
import com.gpb.game.service.StoreAggregatorService;
import com.gpb.game.service.impl.GameServiceImpl;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameServiceImplTest {

    GameRepository gameRepository = mock(GameRepository.class);

    StoreAggregatorService storeAggregatorService = mock(StoreAggregatorService.class);

    GameRepositoryCustom gameRepositoryCustom = mock(GameRepositoryCustom.class);

    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    GameService gameService = new GameServiceImpl(gameRepository, storeAggregatorService,
            gameRepositoryCustom, modelMapper);

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
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gameService.getById(gameId), "app.game.error.id.not.found");
        verify(gameRepository, times(1)).findById(gameId);
    }

    @Test
    void testGetGameById_whenSuccess_shouldReturnGame() {
        long id = 1;
        game.setUserList(new ArrayList<>());
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        GameInfoDto gameInfoDto = modelMapper.map(game, GameInfoDto.class);

        GameInfoDto result = gameService.getDtoById(id);

        assertEquals(gameInfoDto, result);
    }

    @Test
    void testGetByIdWithLoadedUsers_whenNotExist_ShouldThrowException() {
        long gameId = 123L;
        when(gameRepository.findByIdWithUsers(gameId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gameService.getById(gameId), "app.game.error.id.not.found");
        verify(gameRepository, times(1)).findById(gameId);
    }

    @Test
    void testGetGameByIdWithLoadedUsers_whenSuccess_shouldReturnGame() {
        long id = 1;
        game.setUserList(new ArrayList<>());
        when(gameRepository.findByIdWithUsers(id)).thenReturn(Optional.of(game));

        Game result = gameService.getByIdWithLoadedUsers(id);

        assertEquals(game, result);
    }

    @Test
    void testGetGameById_whenNotFound_shouldThrowException() {
        long id = 1;
        when(gameRepository.findByIdWithUsers(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gameService.getDtoById(id), "Game with id '1' not found");
    }

    @Test
    void testGetGameByName_whenSuccess_shouldReturnGame() {
        String name = "name";
        int pageSize = 2;
        int pageNum = 1;
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(gameDto -> modelMapper.map(gameDto, GameDto.class)).toList();
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        when(gameRepositoryCustom.searchByNameFullText(name, pageable))
                .thenReturn(new PageImpl<>(gameList, pageable, 1L));

        GameListPageDto result = gameService.getByName(name, pageSize, pageNum, sort);


        assertEquals(gameListPageDto, result);
    }

    @Test
    void testGetGameByName_whenThatNotRegistered_shouldFindGameFromStoresService() {
        Page page = mock(Page.class);
        String name = "name";
        int pageSize = 2;
        int pageNum = 1;
        game.setName(name);
        List<Game> gameList = Collections.singletonList(game);
        List<Long> gameIds = Collections.singletonList(1L);
        List<GameDto> gameDtoList = gameList.stream().map(gameDto -> modelMapper.map(gameDto, GameDto.class)).toList();
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        when(gameRepositoryCustom.searchByNameFullText(name, PageRequest.of(pageNum - 1, pageSize, sort)))
                .thenReturn(new PageImpl<>(new ArrayList<>(), pageable, 1L));
        when(page.stream()).thenReturn(new ArrayList<>().stream());
        when(storeAggregatorService.findGameByName(name)).thenReturn(Collections.singletonList(game));
        when(gameRepository.findByName(name)).thenReturn(Optional.empty());
        when(gameRepository.save(game)).thenReturn(game);
        when(gameRepository.findAllById(gameIds)).thenReturn(gameList);
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);


        GameListPageDto result = gameService.getByName(name, pageSize, pageNum, sort);


        assertEquals(gameListPageDto, result);
    }

    @Test
    void testFindByGenre_whenSuccess_shouldReturnGameList() {
        int pageSize = 2;
        int pageNum = 2;
        Page page = mock(Page.class);

        List<Genre> genre = Collections.singletonList(Genre.STRATEGIES);
        List<ProductType> types = Collections.singletonList(ProductType.GAME);
        List<ProductType> typesToExclude = List.of(ProductType.ADDITION, ProductType.CURRENCY, ProductType.SUBSCRIPTION);

        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(gameDto -> modelMapper.map(gameDto, GameDto.class)).toList();
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        GameRepositorySearchFilter gameRepositorySearchFilter = GameRepositorySearchFilter.builder()
                .genres(genre)
                .types(types)
                .minPrice(new BigDecimal(0))
                .maxPrice(new BigDecimal(1))
                .build();

        when(gameRepositoryCustom.findGames(gameRepositorySearchFilter, PageRequest.of(pageNum - 1, pageSize, sort))).thenReturn(page);
        when(page.stream()).thenReturn(gameList.stream());
        when(page.getTotalElements()).thenReturn(1L);

        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);


        GameListPageDto result = gameService.getByGenre(genre, typesToExclude, pageSize, pageNum,
                new BigDecimal(0), new BigDecimal(1), sort);


        assertEquals(gameListPageDto, result);
    }

    @Test
    void testFindUserGames_whenSuccess_shouldReturnGameList() {
        int pageSize = 1;
        int pageNum = 1;
        long userId = 1;
        Page page = mock(Page.class);
        List<Game> gameList = Collections.singletonList(game);
        List<GameDto> gameDtoList = gameList.stream().map(gameDto -> modelMapper.map(gameDto, GameDto.class)).toList();
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        GameRepositorySearchFilter gameRepositorySearchFilter = GameRepositorySearchFilter.builder()
                .userId(userId)
                .build();

        when(gameRepositoryCustom.findGames(gameRepositorySearchFilter, PageRequest.of(pageNum - 1, pageSize, sort))).thenReturn(page);        when(page.stream()).thenReturn(gameList.stream());
        when(page.getTotalElements()).thenReturn(1L);
        GameListPageDto gameListPageDto = new GameListPageDto(1, gameDtoList);


        GameListPageDto result = gameService.getUserGames(userId, pageSize, pageNum, sort);


        assertEquals(gameListPageDto, result);
    }

    @Test
    void testRemoveGame_whenSuccess_shouldRemoveGame() {
        long gameId = 1L;

        gameService.removeGame(gameId);

        verify(gameRepository).deleteById(gameId);
    }

    @Test
    void testRemoveGameInStore_whenLastGameInShop_shouldRemovesGame() {
        gameInShop.setGame(game);
        game.setGamesInShop(new HashSet<>(Set.of(gameInShop)));


        gameService.removeGameInShopFromGame(gameInShop);


        verify(gameRepository, times(1)).deleteById(game.getId());
    }

    @Test
    void testRemoveGameInStore_whenMultipleGameInShops_shouldRemovesOnlyShopEntry() {
        GameInShop anotherGameInShop = new GameInShop();
        anotherGameInShop.setId(101L);
        anotherGameInShop.setGame(game);

        gameInShop.setGame(game);
        game.setGamesInShop(new HashSet<>(Set.of(gameInShop, anotherGameInShop)));


        gameService.removeGameInShopFromGame(gameInShop);


        verify(gameRepository, times(1)).save(game);
        verify(gameRepository, never()).deleteById(game.getId());
    }

    @Test
    void testSetFollowGameOption_whenSuccess_shouldSetFollowedOptionToTrue() {
        long gameId = 1L;
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        Game expectedGame = Game.builder()
                .isFollowed(true)
                .gamesInShop(Collections.singleton(gameInShop))
                .build();

        gameService.setFollowGameOption(gameId, true);

        verify(gameRepository).save(expectedGame);
    }
}