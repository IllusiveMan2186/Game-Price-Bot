package com.gpb.web.repository;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.Genre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    Game findById(long gameId);

    Game findByName(String name);

    List<Game> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Game> findAllByGamesInShop_PriceBetween(Pageable pageable, BigDecimal minPrice, BigDecimal maxPrice);

    long countAllByGamesInShop_PriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Game> findByGenresInAndGamesInShop_PriceBetween(List<Genre> genres, Pageable pageable, BigDecimal minPrice, BigDecimal maxPrice);

    long countByGenresIn(List<Genre> genres);
}
