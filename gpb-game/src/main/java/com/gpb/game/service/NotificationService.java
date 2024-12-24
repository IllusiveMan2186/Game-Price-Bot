package com.gpb.game.service;

import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;

import java.util.List;

/**
 * Responsible for sending notification by one specific way
 *
 */
public interface NotificationService {

    /**
     * Send to user by needed type notification type information about game information changes
     *
     * @param user           user that would be informed
     * @param gameInShopList games which information was changed
     */
    void sendGameInfoChange(BasicUser user, List<GameInShop> gameInShopList);
}
