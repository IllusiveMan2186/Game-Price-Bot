package com.gpb.stores.service.impl;

import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.bean.user.BasicUser;
import com.gpb.stores.bean.user.WebUser;
import com.gpb.stores.repository.UserRepository;
import com.gpb.stores.repository.WebUserRepository;
import com.gpb.stores.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WebUserRepository webUserRepository;

    public UserServiceImpl(UserRepository userRepository, WebUserRepository webUserRepository) {
        this.userRepository = userRepository;
        this.webUserRepository = webUserRepository;
    }

    @Override
    public List<BasicUser> getUsersOfChangedGameInfo(List<GameInShop> changedGames) {
        List<Long> changedGamesIds = changedGames.stream()
                .map(GameInShop::getId)
                .toList();
        return userRepository.findSubscribedUserForChangedGames(changedGamesIds);
    }

    public List<WebUser> getWebUsers(List<Long> userIds) {
        return webUserRepository.findAllByIdIn(userIds);
    }
}
