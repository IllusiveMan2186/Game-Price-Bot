package com.gpb.game.repository;

import com.gpb.game.entity.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>, JpaSpecificationExecutor<Game> {

    @Query("""
            SELECT g FROM Game g
            LEFT JOIN FETCH g.userList
            WHERE g.id = :id
            """)
    Optional<Game> findByIdWithUsers(@Param("id") Long id);

    Optional<Game> findByName(String name);
}
