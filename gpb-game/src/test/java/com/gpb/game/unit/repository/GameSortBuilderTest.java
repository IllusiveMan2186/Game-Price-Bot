package com.gpb.game.unit.repository;

import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.repository.advanced.sort.GameSortBuilder;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameSortBuilderTest {

    private CriteriaBuilder cb;
    private Root<Game> root;
    private Subquery<BigDecimal> subquery;
    private CriteriaQuery<Game> cq;
    private Root<GameInShop> shopRoot;
    private Predicate predicatePriceSort;
    private Path pathName;
    private Path pathBigDecimal;

    private GameSortBuilder gameSortBuilder;

    @BeforeEach
    void setUp() {
        gameSortBuilder = new GameSortBuilder();
        cb = mock(CriteriaBuilder.class);
        root = mock(Root.class);
        subquery = mock(Subquery.class);
        cq = mock(CriteriaQuery.class);
        shopRoot = mock(Root.class);
        pathBigDecimal = mock(Path.class);
        shopRoot = mock(Root.class);
        predicatePriceSort = mock(Predicate.class);
        pathName = mock(Path.class);
    }


    @Test
    void testApplySorting_whenWithNoSort_shouldNoCallOrderBy() {
        Pageable pageable = PageRequest.of(0, 10);

        setUpMocks(cb, root, null, subquery, cq, null, shopRoot);


        gameSortBuilder.applySorting(cq, cb, root, pageable);


        verify(cq, never()).orderBy(any(Order.class));
    }

    @Test
    void testApplySorting_whenWithSortByPriceAscending_shouldReturnsGamesSortedByPrice() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "gamesInShop.discountPrice"));

        setUpMocks(cb, root, predicatePriceSort, subquery, cq, pathBigDecimal, shopRoot);


        gameSortBuilder.applySorting(cq, cb, root, pageable);


        verify(subquery, times(1)).where(predicatePriceSort);
        verify(cb, times(1)).asc(subquery);
    }

    @Test
    void testApplySorting_whenWithSortByPriceDescending_shouldReturnsGamesSortedByPrice() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "gamesInShop.discountPrice"));

        setUpMocks(cb, root, predicatePriceSort, subquery, cq,
                pathBigDecimal, shopRoot);


        gameSortBuilder.applySorting(cq, cb, root, pageable);


        verify(subquery, times(1)).where(predicatePriceSort);
        verify(cb, times(1)).desc(subquery);
    }

    @Test
    void testApplySorting_whenWithSortByNameDescending_shouldReturnsGamesSortedByName() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));

        setUpMocks(cb, root, null, subquery, cq, null, shopRoot);
        when(root.get("name")).thenReturn(pathName);


        gameSortBuilder.applySorting(cq, cb, root, pageable);


        verify(cb, times(1)).desc(pathName);
    }

    @Test
    void testApplySorting_whenWithSortByNameAscending_shouldReturnsGamesSortedByName() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

        setUpMocks(cb, root, null, subquery, cq, null, shopRoot);
        when(root.get("name")).thenReturn(pathName);


        gameSortBuilder.applySorting(cq, cb, root, pageable);


        verify(cb, times(1)).asc(pathName);
    }

    private void setUpMocks(CriteriaBuilder cb,
                            Root<Game> root,
                            Predicate predicatePriceSort,
                            Subquery<BigDecimal> subquery,
                            CriteriaQuery<Game> cq,
                            Path pathBigDecimal,
                            Root<GameInShop> shopRoot) {

        if (predicatePriceSort != null) {
            when(cq.subquery(BigDecimal.class)).thenReturn(subquery);
            when(subquery.from(GameInShop.class)).thenReturn(shopRoot);

            when(shopRoot.get("game")).thenReturn(pathBigDecimal);
            when(cb.equal(pathBigDecimal, root)).thenReturn(predicatePriceSort);
        }
    }
}
