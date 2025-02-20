package com.gpb.game.unit.service.impl;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.game.configuration.MapperConfig;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.repository.GameInShopRepository;
import com.gpb.game.repository.GameRepositoryCustom;
import com.gpb.game.service.GameInShopService;
import com.gpb.game.service.GameService;
import com.gpb.game.service.GameStoresService;
import com.gpb.game.service.impl.GameInShopServiceImpl;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameInShopServiceImplTest {

    GameService gameService = mock(GameService.class);
    GameInShopRepository gameInShopRepository = mock(GameInShopRepository.class);

    GameStoresService gameStoresService = mock(GameStoresService.class);

    GameRepositoryCustom gameRepositoryCustom = mock(GameRepositoryCustom.class);

    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    GameInShopService gameInShopService = new GameInShopServiceImpl(gameService, gameInShopRepository, gameStoresService,
            modelMapper);

    private final GameInShop gameInShop = GameInShop.builder()
            .price(new BigDecimal(2))
            .discountPrice(new BigDecimal(1))
            .build();


    private final Game game = Game.builder()
            .isFollowed(false)
            .gamesInShop(Collections.singleton(gameInShop))
            .build();

    @Test
    void testGetAllGamesInShop_whenNotExist_ShouldThrowException() {
        when(gameInShopRepository.findAll()).thenReturn(List.of(gameInShop));

        List<GameInShop> result = gameInShopService.getAllGamesInShop();

        assertEquals(1, result.size());
        assertEquals(gameInShop, result.get(0));
    }

    @Test
    void testGetSubscribedGames_whenSuccess_thenShouldGetGames() {
        List<GameInShop> games = new ArrayList<>();
        when(gameInShopRepository.findSubscribedGames()).thenReturn(games);

        List<GameInShop> result = gameInShopService.getSubscribedGames();

        assertEquals(games, result);
    }

    @Test
    void testGetGameByUrl_whenGameWithUrlAlreadyRegistered_shouldReturnRegisteredGame() {
        String url = "url";
        GameInShop gameInShop = GameInShop.builder().game(game).build();
        when(gameInShopRepository.findByUrl(url)).thenReturn(gameInShop);
        GameInfoDto gameInfoDto = modelMapper.map(game, GameInfoDto.class);

        GameInfoDto result = gameInShopService.getByUrl(url);

        assertEquals(gameInfoDto, result);
    }

    @Test
    void testGetGameByUrl_whenNotRegistered_shouldReturnGame() {
        String url = "url";
        String name = "name";
        Game gameWithAddedShop = Game.builder().gamesInShop(new HashSet<>()).build();

        when(gameInShopRepository.findByUrl(url)).thenReturn(null);
        when(gameStoresService.findGameByUrl(url)).thenReturn(game);
        game.setName(name);
        when(gameService.getByUrl(url)).thenReturn(gameWithAddedShop);
        GameInfoDto gameInfoDto = modelMapper.map(gameWithAddedShop, GameInfoDto.class);

        GameInfoDto result = gameInShopService.getByUrl(url);

        assertEquals(gameInfoDto, result);
    }

    @Test
    void testAddGameInStore_whenSuccess_shouldReturnGameWithNewGameInStore() {
        String url = "url";
        long gameId = 1L;
        Game gameAfterAdding = Game.builder().gamesInShop(Set.of(gameInShop)).build();
        when(gameService.getById(gameId)).thenReturn(game, gameAfterAdding);
        when(gameStoresService.findGameInShopByUrl(url)).thenReturn(gameInShop);
        GameInfoDto gameInfoDto = modelMapper.map(gameAfterAdding, GameInfoDto.class);

        GameInfoDto result = gameInShopService.addGameInStore(gameId, url);

        assertEquals(gameInfoDto, result);
        gameInShop.setGame(game);
        verify(gameInShopRepository).save(gameInShop);
    }

    @Test
    void testChangeInfo_whenSuccess_thenSaveChanges() {
        List<GameInShop> changedGames = new ArrayList<>();

        gameInShopService.changeInfo(changedGames);

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

        List<GameInShop> result = gameInShopService.getUsersChangedGames(user, List.of(gameInShop1, gameInShop2));

        assertEquals(changedGames, result);
    }

    @Test
    void testRemoveGameInStore_whenSuccess_shouldRemoveGameInStore() {
        long gameInStoreId = 1L;
        gameInShop.setGame(game);
        gameInShop.setId(gameInStoreId);
        game.setGamesInShop(new HashSet<>(Set.of(gameInShop)));
        when(gameInShopRepository.findById(gameInStoreId)).thenReturn(Optional.of(gameInShop));


        gameInShopService.removeGameInStore(gameInStoreId);


        verify(gameService).removeGameInShopFromGame(gameInShop);
        verify(gameInShopRepository).deleteById(gameInStoreId);
    }

    @Test
    void testRemoveGameInStore_whenGameNotFound_shouldNotCallDelete() {
        long gameInStoreId = 200L;
        when(gameInShopRepository.findById(gameInStoreId)).thenReturn(Optional.empty());

        gameInShopService.removeGameInStore(gameInStoreId);

        verify(gameInShopRepository, never()).deleteById(anyLong());
        verify(gameService, never()).removeGameInShopFromGame(any());
    }
}
