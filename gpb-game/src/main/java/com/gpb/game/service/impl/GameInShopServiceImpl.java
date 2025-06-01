package com.gpb.game.service.impl;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.repository.GameInShopRepository;
import com.gpb.game.service.GameInShopService;
import com.gpb.game.service.GameService;
import com.gpb.game.service.StoreAggregatorService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor

public class GameInShopServiceImpl implements GameInShopService {

    private final GameService gameService;
    private final GameInShopRepository gameInShopRepository;
    private final StoreAggregatorService storeAggregatorService;
    private final ModelMapper modelMapper;

    @Override
    public GameInfoDto getByUrl(String url) {
        log.info("Get game from store by url : {}", url);

        final GameInShop gameInShop = gameInShopRepository.findByUrl(url);
        if (gameInShop == null) {
            return modelMapper.map(storeAggregatorService.findGameByUrl(url), GameInfoDto.class);
        }

        return modelMapper.map(gameInShop.getGame(), GameInfoDto.class);
    }

    @Override
    public List<GameInShop> getAllGamesInShop() {
        log.debug("Get all games in shop");
        return gameInShopRepository.findAll();
    }

    @Override
    public GameInfoDto addGameInStore(long gameId, String url) {
        log.info("Get game in store by url {} and adding to game {}", url, gameId);

        final Game game = gameService.getById(gameId);
        final GameInShop gameInShop = storeAggregatorService.findGameInShopByUrl(url);
        gameInShop.setGame(game);
        gameInShopRepository.save(gameInShop);
        return modelMapper.map(gameService.getById(gameId), GameInfoDto.class);
    }

    @Override
    public List<GameInShop> getSubscribedGames() {
        log.info("Get game for which subscribe users");
        return gameInShopRepository.findSubscribedGames();
    }

    @Override
    public void changeInfo(List<GameInShop> changedGames) {
        log.info("Save games in store changes for {} elements", changedGames.size());

        gameInShopRepository.saveAll(changedGames);
    }

    @Override
    public List<GameInShop> getUsersChangedGames(BasicUser user, List<GameInShop> changedGames) {
        List<Long> changedGamesIds = changedGames.stream()
                .map(GameInShop::getId)
                .toList();
        return gameInShopRepository.findSubscribedGames(user.getId(), changedGamesIds);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeGameInStore(long gameInStoreId) {
        log.info("Remove game in store by id : {}", gameInStoreId);
        Optional<GameInShop> optionalGameInShop = gameInShopRepository.findById(gameInStoreId);
        if (optionalGameInShop.isEmpty()) {
            log.error("Game with id '{}' not found.", gameInStoreId);
            return;
        }
        GameInShop gameInShop = optionalGameInShop.get();

        gameService.removeGameInShopFromGame(gameInShop);
        gameInShopRepository.deleteById(gameInShop.getId());
    }
}
