package com.gpb.stores.repository;

import com.gpb.stores.bean.user.BasicUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<BasicUser, Long> {

    @Query(value = "SELECT u FROM BasicUser u join fetch u.gameList g join fetch g.gamesInShop gs WHERE gs.id in :ids")
    List<BasicUser> findSubscribedUserForChangedGames(@Param("ids") List<Long> changedGamesIds);
}
