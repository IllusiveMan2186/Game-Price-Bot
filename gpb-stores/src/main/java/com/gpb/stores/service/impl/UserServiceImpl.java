package com.gpb.stores.service.impl;

import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.bean.user.BasicUser;
import com.gpb.stores.exception.NotFoundException;
import com.gpb.stores.repository.UserRepository;
import com.gpb.stores.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public BasicUser createUser() {
        return userRepository.save(new BasicUser());
    }

    @Override
    public List<BasicUser> getUsersOfChangedGameInfo(List<GameInShop> changedGames) {
        List<Long> changedGamesIds = changedGames.stream()
                .map(GameInShop::getId)
                .toList();
        return userRepository.findSubscribedUserForChangedGames(changedGamesIds);
    }

    @Override
    @Transactional
    public void subscribeToGame(long userId, long gameId) {
        log.info(String.format("Subscribe for game(%s) into user(%s) game list", gameId, userId));

        userRepository.addGameToUserListOfGames(userId, gameId);
    }

    @Override
    public void unsubscribeFromGame(long userId, long gameId) {
        log.info(String.format("Unsubscribe game(%s) from user(%s) game list", gameId, userId));

        userRepository.removeGameFromUserListOfGames(userId, gameId);
    }
}
