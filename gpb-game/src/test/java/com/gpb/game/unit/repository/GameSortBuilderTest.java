package com.gpb.game.unit.repository;

import com.gpb.game.entity.game.Game;
import com.gpb.game.repository.advanced.sort.GameSortBuilder;
import com.gpb.game.util.Constants;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameSortBuilderTest {

    private CriteriaBuilder cb;
    private Root<Game> root;
    private CriteriaQuery<Game> cq;
    private Path path;

    private GameSortBuilder gameSortBuilder;

    @BeforeEach
    void setUp() {
        gameSortBuilder = new GameSortBuilder();
        cb = mock(CriteriaBuilder.class);
        root = mock(Root.class);
        cq = mock(CriteriaQuery.class);
        path = mock(Path.class);
    }


    @Test
    void testApplySorting_whenWithNoSort_shouldNoCallOrderBy() {
        Pageable pageable = PageRequest.of(0, 10);


        gameSortBuilder.applySorting(cq, cb, root, pageable);


        verify(cq, never()).orderBy(any(Order.class));
    }

    @Test
    void testApplySorting_whenWithSortByPriceAscending_shouldReturnsGamesSortedByPrice() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "gamesInShop.discountPrice"));

        Order order = setUpMocks(cb, root, true, Constants.MIN_DISCOUNT_PRICE_FORMULA_FIELD);


        gameSortBuilder.applySorting(cq, cb, root, pageable);


        verify(root, times(1)).get(Constants.MIN_DISCOUNT_PRICE_FORMULA_FIELD);
        verify(cq, times(1)).orderBy(List.of(order));
    }

    @Test
    void testApplySorting_whenWithSortByPriceDescending_shouldReturnsGamesSortedByPrice() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "gamesInShop.discountPrice"));

        Order order = setUpMocks(cb, root, false, Constants.MAX_DISCOUNT_PRICE_FORMULA_FIELD);


        gameSortBuilder.applySorting(cq, cb, root, pageable);


        verify(root, times(1)).get(Constants.MAX_DISCOUNT_PRICE_FORMULA_FIELD);
        verify(cq, times(1)).orderBy(List.of(order));
    }

    @Test
    void testApplySorting_whenWithSortByNameDescending_shouldReturnsGamesSortedByName() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));

        Order order = setUpMocks(cb, root, false, "name");


        gameSortBuilder.applySorting(cq, cb, root, pageable);


        verify(cb, times(1)).desc(path);
        verify(cq, times(1)).orderBy(List.of(order));
    }

    @Test
    void testApplySorting_whenWithSortByNameAscending_shouldReturnsGamesSortedByName() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

        Order order = setUpMocks(cb, root, true, "name");


        gameSortBuilder.applySorting(cq, cb, root, pageable);


        verify(cb, times(1)).asc(path);
        verify(cq, times(1)).orderBy(List.of(order));
    }

    private Order setUpMocks(CriteriaBuilder cb,
                             Root<Game> root,
                             boolean isAscending,
                             String property) {

        Order order = mock(Order.class);

        when(root.get(property)).thenReturn(path);

        if (isAscending) {
            when(cb.asc(path)).thenReturn(order);
        } else {
            when(cb.desc(path)).thenReturn(order);
        }
        return order;
    }
}
