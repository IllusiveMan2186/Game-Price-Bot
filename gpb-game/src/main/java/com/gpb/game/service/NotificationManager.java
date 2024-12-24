package com.gpb.game.service;

import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;

import java.util.List;

public interface NotificationManager {

    /**
     * Send to user by needed notification types that user chose information about game information changes
     *
     * @param user           user that would be informed
     * @param gameInShopList games which information was changed
     */
    void sendGameInfoChange(BasicUser user, List<GameInShop> gameInShopList);
}
