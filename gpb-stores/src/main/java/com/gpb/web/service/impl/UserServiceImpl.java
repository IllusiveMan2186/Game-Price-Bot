package com.gpb.web.service.impl;

import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.BasicUser;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.repository.UserRepository;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
