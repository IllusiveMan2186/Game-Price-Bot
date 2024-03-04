package com.gpb.web.service;

import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.WebUser;

import java.util.List;

public interface EmailService {

    void sendGameInfoChange(WebUser user, List<GameInShop> gameInShopList);
}
