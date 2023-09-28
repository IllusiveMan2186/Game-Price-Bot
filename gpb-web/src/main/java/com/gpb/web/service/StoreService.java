package com.gpb.web.service;


import com.gpb.web.bean.Game;
import com.gpb.web.bean.GameInShop;

public interface StoreService {

    Game findUncreatedGameByUrl(String url);

    GameInShop findByUrl(String url);

    Game findUncreatedGameByName(String name);

    GameInShop findByName(String name);
}
