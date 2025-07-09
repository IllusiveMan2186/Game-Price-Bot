package com.gpb.game.repository;

import com.gpb.game.entity.user.AccountLinker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountLinkerRepository extends JpaRepository<AccountLinker, String> {

    @Query("""
            SELECT a FROM AccountLinker a
            JOIN FETCH a.user u
            LEFT JOIN FETCH u.notificationTypes
            WHERE a.token = :token
            """)
    Optional<AccountLinker> findByIdWithUserAndNotifications(@Param("token") String token);


    void deleteByUserId(long userId);
}
