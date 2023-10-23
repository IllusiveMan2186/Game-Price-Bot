package com.gpb.web.repository;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.Genre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    Game findById(long gameId);

    Game findByName(String name);

    List<Game> findByGenresIn(List<Genre> genres, Pageable pageable);
}
