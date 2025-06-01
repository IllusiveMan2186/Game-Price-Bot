package com.gpb.game.repository;

import com.gpb.game.entity.user.BasicUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<BasicUser, Long> {

    @Query("SELECT u FROM BasicUser u LEFT JOIN FETCH u.gameList WHERE u.id = :userId")
    Optional<BasicUser> findByIdWithGames(@Param("userId") long userId);

    @Modifying
    @Query(value = "INSERT INTO user_game(user_id,game_id) VALUES(:userId,:gameId); ", nativeQuery = true)
    void addGameToUserListOfGames(@Param("userId") long userId, @Param("gameId") long gameId);

    @Modifying
    @Query(value = "DELETE FROM user_game WHERE user_id=:userId AND game_id=(:gameId); ", nativeQuery = true)
    void removeGameFromUserListOfGames(@Param("userId") long userId, @Param("gameId") long gameId);

    @Query(value = "SELECT u FROM BasicUser u join fetch u.gameList g join fetch g.gamesInShop gs WHERE gs.id in :ids")
    List<BasicUser> findSubscribedUserForChangedGames(@Param("ids") List<Long> changedGamesIds);
}
