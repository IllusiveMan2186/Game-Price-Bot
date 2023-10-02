package com.gpb.web.repository;

import com.gpb.web.bean.user.WebUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface WebUserRepository extends CrudRepository<WebUser, Long> {


    WebUser findById(long userId);

    WebUser findByEmail(String email);

    @Modifying
    @Query(value = "insert into user_game(user_id,game_id) VALUES(:userId,:gameId); ", nativeQuery = true)
    void addGameToUserListOfGames(@Param("userId") long userId, @Param("gameId") long gameId);
}
