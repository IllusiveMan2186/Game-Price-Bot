package com.gpb.game.unit.repository;

import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameRepositorySearchFilter;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.repository.predicate.GamePredicateBuilder;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GamePredicateBuilderTest {

    private GamePredicateBuilder predicateBuilder;

    private CriteriaBuilder cb;
    private Root<Game> root;
    private Join<Game, GameInShop> shopJoin;

    @BeforeEach
    void setUp() {
        predicateBuilder = new GamePredicateBuilder();
        cb = mock(CriteriaBuilder.class);
        root = mock(Root.class);
        shopJoin = mock(Join.class);
    }

    @Test
    void testFindGames_whenByGenres_thenReturnsGenrePredicate() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .genres(List.of(Genre.ACTION, Genre.RPG))
                .build();

        Join<Object, Object> genreJoin = mock(Join.class);
        when(root.join(eq("genres"), any())).thenReturn(genreJoin);
        Predicate genrePredicate = mock(Predicate.class);
        when(genreJoin.in(filter.getGenres())).thenReturn(genrePredicate);

        List<Predicate> predicates = predicateBuilder.buildFilters(cb, root, shopJoin, filter);

        assertEquals(1, predicates.size());
        assertEquals(genrePredicate, predicates.get(0));
    }

    @Test
    void testFindGames_whenByTypes_thenReturnsTypePredicate() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .types(List.of(ProductType.ADDITION, ProductType.GAME))
                .build();

        Path<Object> typePath = mock(Path.class);
        when(root.get("type")).thenReturn(typePath);
        Predicate typePredicate = mock(Predicate.class);
        when(typePath.in(filter.getTypes())).thenReturn(typePredicate);

        List<Predicate> predicates = predicateBuilder.buildFilters(cb, root, shopJoin, filter);

        assertEquals(1, predicates.size());
        assertEquals(typePredicate, predicates.get(0));
    }

    @Test
    void testFindGames_whenByPriceRange_thenReturnsGameInPriceRange() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .maxPrice(BigDecimal.valueOf(30))
                .minPrice(BigDecimal.valueOf(10))
                .build();

        Path pricePath = mock(Path.class);
        when(shopJoin.get("discountPrice")).thenReturn(pricePath);
        Predicate pricePredicate = mock(Predicate.class);
        when(cb.between(pricePath, filter.getMinPrice(), filter.getMaxPrice())).thenReturn(pricePredicate);

        List<Predicate> predicates = predicateBuilder.buildFilters(cb, root, shopJoin, filter);

        assertEquals(1, predicates.size());
        assertEquals(pricePredicate, predicates.get(0));
    }

    @Test
    void testFindGames_whenByUserId_thenReturnsUserIdPredicate() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .userId(123L)
                .build();

        Join userJoin = mock(Join.class);
        Path<Object> userIdPath = mock(Path.class);
        Predicate userIdPredicate = mock(Predicate.class);

        when(root.join("userList", JoinType.INNER)).thenReturn(userJoin);
        when(userJoin.get("id")).thenReturn(userIdPath);
        when(cb.equal(userIdPath, filter.getUserId())).thenReturn(userIdPredicate);

        List<Predicate> predicates = predicateBuilder.buildFilters(cb, root, shopJoin, filter);

        assertEquals(1, predicates.size());
        assertEquals(userIdPredicate, predicates.get(0));
    }

    @Test
    void testFindGames_whenAllFilters_thenReturnsAllPredicates() {

        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .maxPrice(BigDecimal.valueOf(30))
                .minPrice(BigDecimal.valueOf(10))
                .types(List.of(ProductType.ADDITION))
                .genres(List.of(Genre.ACTION))
                .build();

        Join<Object, Object> genreJoin = mock(Join.class);
        when(root.join(eq("genres"), any())).thenReturn(genreJoin);
        when(genreJoin.in(filter.getGenres())).thenReturn(mock(Predicate.class));

        Path<Object> typePath = mock(Path.class);
        when(root.get("type")).thenReturn(typePath);
        when(typePath.in(filter.getTypes())).thenReturn(mock(Predicate.class));

        Path pricePath = mock(Path.class);
        when(shopJoin.get("discountPrice")).thenReturn(pricePath);
        when(cb.between(pricePath, filter.getMinPrice(), filter.getMaxPrice())).thenReturn(mock(Predicate.class));

        Join userJoin = mock(Join.class);
        Path<Object> userIdPath = mock(Path.class);
        when(root.join("userList", JoinType.INNER)).thenReturn(userJoin);
        when(userJoin.get("id")).thenReturn(userIdPath);
        when(cb.equal(userIdPath, filter.getUserId())).thenReturn(mock(Predicate.class));

        List<Predicate> predicates = predicateBuilder.buildFilters(cb, root, shopJoin, filter);

        assertEquals(3, predicates.size());
    }
}