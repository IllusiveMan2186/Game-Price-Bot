package com.gpb.web.repository;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.bean.user.BasicUser;
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

    long countAllByNameContainingIgnoreCase(String name);

    List<Game> findAllByGamesInShop_DiscountPriceBetween(Pageable pageable, BigDecimal minPrice, BigDecimal maxPrice);

    long countAllByGamesInShop_DiscountPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Game> findByGenresInAndGamesInShop_DiscountPriceBetween(List<Genre> genres, Pageable pageable, BigDecimal minPrice, BigDecimal maxPrice);

    long countAllByUserList(BasicUser user);

    List<Game> findByUserList(BasicUser user, Pageable pageable);

    long countByGenresIn(List<Genre> genres);
}
