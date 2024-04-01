package com.gpb.web.repository;

import com.gpb.web.bean.user.WebUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WebUserRepository extends CrudRepository<WebUser, Long> {

    Optional<WebUser> findById(long userId);

    Optional<WebUser> findByEmail(String email);

    @Modifying
    @Query(value = "INSERT INTO user_game(user_id,game_id) VALUES(:userId,:gameId); ", nativeQuery = true)
    void addGameToUserListOfGames(@Param("userId") long userId, @Param("gameId") long gameId);

    @Modifying
    @Query(value = "DELETE FROM user_game WHERE user_id=:userId AND game_id=(:gameId); ", nativeQuery = true)
    void removeGameFromUserListOfGames(@Param("userId") long userId, @Param("gameId") long gameId);
}