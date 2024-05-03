package com.gpb.telegram.repository;

import com.gpb.telegram.bean.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    List<Game> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Game> findByIdIn(List<Long> iidList, Pageable pageable);

    long countAllByNameContainingIgnoreCase(String name);
}
