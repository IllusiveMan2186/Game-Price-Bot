package com.gpb.web.repository;

import com.gpb.web.bean.game.GameInShop;
import org.springframework.data.repository.CrudRepository;

public interface GameInShopRepository extends CrudRepository<GameInShop, Long> {

    GameInShop findById(long gameInShopId);

    GameInShop findByUrl(String url);
}
