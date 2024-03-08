package com.gpb.web.service;

import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.BasicUser;
import com.gpb.web.bean.user.WebUser;

import java.util.List;

/**
 * Class for handling users
 */
public interface UserService {

    /**
     * @param changedGames changed games
     * @return users that subscribe to changed game
     */
    List<BasicUser> getUsersOfChangedGameInfo(List<GameInShop> changedGames);

    List<WebUser> getWebUsers(List<Long> ids);
}
