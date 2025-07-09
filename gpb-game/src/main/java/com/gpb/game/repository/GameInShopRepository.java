package com.gpb.game.repository;

import com.gpb.game.entity.game.GameInShop;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameInShopRepository extends JpaRepository<GameInShop, Long> {

    GameInShop findByUrl(String url);

    @Query(value = """
            SELECT gs FROM GameInShop gs
            JOIN FETCH gs.game g
            JOIN FETCH g.userList u
            """)
    List<GameInShop> findSubscribedGames();

    @EntityGraph(attributePaths = {})
    @Query(value = """
            SELECT gs FROM GameInShop gs
            JOIN FETCH gs.game g
            JOIN FETCH g.userList u
            WHERE gs.id in :ids
            AND u.id=:userId
            """)
    List<GameInShop> findSubscribedGames(@Param("userId") long userId, @Param("ids") List<Long> changedGamesIds);
}
