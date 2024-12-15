package com.gpb.game.repository;

import com.gpb.game.bean.game.Game;
import com.gpb.game.bean.game.Genre;
import com.gpb.game.bean.game.ProductType;
import com.gpb.game.bean.user.BasicUser;
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

    List<Game> findAllByTypeInAndGamesInShop_DiscountPriceBetween(Pageable pageable, List<ProductType> type
            , BigDecimal minPrice, BigDecimal maxPrice);

    long countAllByTypeInAndGamesInShop_DiscountPriceBetween(List<ProductType> type, BigDecimal minPrice, BigDecimal maxPrice);

    List<Game> findByGenresInAndTypeInAndGamesInShop_DiscountPriceBetween(List<Genre> genres, List<ProductType> type,
                                                                          Pageable pageable, BigDecimal minPrice,
                                                                          BigDecimal maxPrice);

    long countAllByUserList(BasicUser user);

    List<Game> findByUserList(BasicUser user, Pageable pageable);

    long countByGenresInAndTypeIn(List<Genre> genres, List<ProductType> type);
}
