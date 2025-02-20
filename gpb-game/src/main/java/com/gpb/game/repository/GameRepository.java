package com.gpb.game.repository;

import com.gpb.game.entity.game.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    Game findById(long gameId);

    Game findByName(String name);
}
