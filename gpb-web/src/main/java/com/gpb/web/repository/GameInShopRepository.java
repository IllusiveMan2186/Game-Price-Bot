package com.gpb.web.repository;

import com.gpb.web.bean.game.GameInShop;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameInShopRepository extends CrudRepository<GameInShop, Long> {

    GameInShop findById(long gameInShopId);

    GameInShop findByUrl(String url);

    @Query(value = "SELECT gs FROM GameInShop gs join fetch gs.game g join fetch g.userList u ")
    List<GameInShop> findSubscribedGames();

    @Query(value = "SELECT gs FROM GameInShop gs join fetch gs.game g join fetch g.userList u WHERE gs.id in :ids AND u.id=:userId")
    List<GameInShop> findSubscribedGames(@Param("userId") long userId, @Param("ids") List<Long> changedGamesIds);
}
