package com.gpb.game.service.impl;

import com.gpb.game.bean.game.GameInShop;
import com.gpb.game.bean.user.AccountLinker;
import com.gpb.game.bean.user.BasicUser;
import com.gpb.game.exception.NotExistingMessengerActivationTokenException;
import com.gpb.game.exception.NotFoundException;
import com.gpb.game.repository.AccountLinkerRepository;
import com.gpb.game.repository.UserRepository;
import com.gpb.game.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final AccountLinkerRepository accountLinkerRepository;
    private final UserRepository userRepository;

    public UserServiceImpl(AccountLinkerRepository accountLinkerRepository, UserRepository userRepository) {
        this.accountLinkerRepository = accountLinkerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BasicUser getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Target user not found with ID: " + userId));
    }

    @Override
    public BasicUser createUser() {
        return userRepository.save(new BasicUser());
    }

    @Override
    public void linkUsers(String token, long sourceUserId) {
        AccountLinker connector = accountLinkerRepository.findById(token)
                .orElseThrow(NotExistingMessengerActivationTokenException::new);
        BasicUser targetUser = connector.getUser();
        BasicUser sourceUser = userRepository.findById(sourceUserId)
                .orElseThrow(() -> new NotFoundException("Source user not found with ID: " + sourceUserId));

        targetUser.getGameList().addAll(sourceUser.getGameList());
        targetUser.getNotificationTypes().addAll(sourceUser.getNotificationTypes());

        userRepository.save(targetUser);
        userRepository.deleteById(sourceUser.getId());
        accountLinkerRepository.deleteById(token);
    }

    @Override
    public String getAccountLinkerToken(long userId) {
        AccountLinker connector = new AccountLinker();
        BasicUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Target user not found with ID: " + userId));
        connector.setUser(user);
        return accountLinkerRepository.save(connector).getToken();
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
        log.info("Subscribe for game({}) into user({}) game list", gameId, userId);

        userRepository.addGameToUserListOfGames(userId, gameId);
    }

    @Override
    public void unsubscribeFromGame(long userId, long gameId) {
        log.info("Unsubscribe game({}) from user({}) game list", gameId, userId);

        userRepository.removeGameFromUserListOfGames(userId, gameId);
    }
}
