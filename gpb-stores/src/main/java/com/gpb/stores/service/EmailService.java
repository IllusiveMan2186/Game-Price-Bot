package com.gpb.stores.service;

import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.bean.user.WebUser;

import java.util.List;

public interface EmailService {

    void sendGameInfoChange(WebUser user, List<GameInShop> gameInShopList);
}
