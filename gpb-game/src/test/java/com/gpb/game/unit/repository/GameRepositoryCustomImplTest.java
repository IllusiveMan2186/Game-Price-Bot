package com.gpb.game.unit.repository;

import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameRepositorySearchFilter;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.repository.impl.GameRepositoryCustomImpl;
import com.gpb.game.repository.predicate.GamePredicateBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameRepositoryCustomImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private GamePredicateBuilder predicateBuilder;

    @InjectMocks
    private GameRepositoryCustomImpl gameRepository;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);
        gameRepository = new GameRepositoryCustomImpl(predicateBuilder);
        Field entityManagerField = GameRepositoryCustomImpl.class.getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(gameRepository, entityManager);
    }

    @Test
    void testFindGames_whenWithNoSort_thenNoCallOrderBy() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder().build();
        Pageable pageable = PageRequest.of(0, 10);

        List<Game> games = new ArrayList<>();
        Long totalRecords = 1L;

        TypedQuery<Game> tq = mock(TypedQuery.class);
        TypedQuery<Long> countTq = mock(TypedQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Game> root = mock(Root.class);
        Join<Game, GameInShop> join = mock(Join.class);
        Subquery<BigDecimal> subquery = mock(Subquery.class);
        CriteriaQuery<Game> cq = mock(CriteriaQuery.class);
        CriteriaQuery<Long> countCq = mock(CriteriaQuery.class);
        Root<GameInShop> shopRoot = mock(Root.class);

        setUpMocks(pageable, games, totalRecords, cb, root, join, null, subquery, cq,
                countCq, null, shopRoot, tq, countTq);

        Page<Game> result = gameRepository.findGames(filter, pageable);

        assertEquals(games, result.getContent());
        assertEquals(totalRecords, result.getTotalElements());
        verify(cq, never()).orderBy(any(Order.class));
    }

    @Test
    void testFindGames_whenWithSortByPriceAscending_thenReturnsGamesSortedByPrice() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder().build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "gamesInShop.discountPrice"));

        List<Game> games = new ArrayList<>();
        Long totalRecords = 1L;

        TypedQuery<Game> tq = mock(TypedQuery.class);
        TypedQuery<Long> countTq = mock(TypedQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Game> root = mock(Root.class);
        Join<Game, GameInShop> join = mock(Join.class);
        Predicate predicatePriceSort = mock(Predicate.class);
        Subquery<BigDecimal> subquery = mock(Subquery.class);
        CriteriaQuery<Game> cq = mock(CriteriaQuery.class);
        CriteriaQuery<Long> countCq = mock(CriteriaQuery.class);
        Path pathBigDecimal = mock(Path.class);
        Root<GameInShop> shopRoot = mock(Root.class);

        setUpMocks(pageable, games, totalRecords, cb, root, join, predicatePriceSort, subquery, cq,
                countCq, pathBigDecimal, shopRoot, tq, countTq);

        Page<Game> result = gameRepository.findGames(filter, pageable);


        assertEquals(games, result.getContent());
        assertEquals(totalRecords, result.getTotalElements());
        verify(subquery, times(1)).where(predicatePriceSort);
        verify(cb, times(1)).asc(subquery);
    }

    @Test
    void testFindGames_whenWithSortByPriceDescending_thenReturnsGamesSortedByPrice() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder().build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "gamesInShop.discountPrice"));

        List<Game> games = new ArrayList<>();
        Long totalRecords = 1L;

        TypedQuery<Game> tq = mock(TypedQuery.class);
        TypedQuery<Long> countTq = mock(TypedQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Game> root = mock(Root.class);
        Join<Game, GameInShop> join = mock(Join.class);
        Predicate predicatePriceSort = mock(Predicate.class);
        Subquery<BigDecimal> subquery = mock(Subquery.class);
        CriteriaQuery<Game> cq = mock(CriteriaQuery.class);
        CriteriaQuery<Long> countCq = mock(CriteriaQuery.class);
        Path pathBigDecimal = mock(Path.class);
        Root<GameInShop> shopRoot = mock(Root.class);

        setUpMocks(pageable, games, totalRecords, cb, root, join, predicatePriceSort, subquery, cq,
                countCq, pathBigDecimal, shopRoot, tq, countTq);

        Page<Game> result = gameRepository.findGames(filter, pageable);


        assertEquals(games, result.getContent());
        assertEquals(totalRecords, result.getTotalElements());
        verify(subquery, times(1)).where(predicatePriceSort);
        verify(cb, times(1)).desc(subquery);
    }

    @Test
    void testFindGames_whenWithSortByNameDescending_thenReturnsGamesSortedByName() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder().build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));

        List<Game> games = new ArrayList<>();
        Long totalRecords = 1L;

        TypedQuery<Game> tq = mock(TypedQuery.class);
        TypedQuery<Long> countTq = mock(TypedQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Game> root = mock(Root.class);
        Join<Game, GameInShop> join = mock(Join.class);
        Subquery<BigDecimal> subquery = mock(Subquery.class);
        CriteriaQuery<Game> cq = mock(CriteriaQuery.class);
        CriteriaQuery<Long> countCq = mock(CriteriaQuery.class);
        Path pathName = mock(Path.class);
        Root<GameInShop> shopRoot = mock(Root.class);

        setUpMocks(pageable, games, totalRecords, cb, root, join, null, subquery, cq,
                countCq, null, shopRoot, tq, countTq);
        when(root.get("name")).thenReturn(pathName);


        Page<Game> result = gameRepository.findGames(filter, pageable);


        assertEquals(games, result.getContent());
        assertEquals(totalRecords, result.getTotalElements());
        verify(cb, times(1)).desc(pathName);
    }

    @Test
    void testFindGames_whenWithSortByNameAscending_thenReturnsGamesSortedByName() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder().build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

        List<Game> games = new ArrayList<>();
        Long totalRecords = 1L;

        TypedQuery<Game> tq = mock(TypedQuery.class);
        TypedQuery<Long> countTq = mock(TypedQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Game> root = mock(Root.class);
        Join<Game, GameInShop> join = mock(Join.class);
        Subquery<BigDecimal> subquery = mock(Subquery.class);
        CriteriaQuery<Game> cq = mock(CriteriaQuery.class);
        CriteriaQuery<Long> countCq = mock(CriteriaQuery.class);
        Path pathName = mock(Path.class);
        Root<GameInShop> shopRoot = mock(Root.class);

        setUpMocks(pageable, games, totalRecords, cb, root, join, null, subquery, cq,
                countCq, null, shopRoot, tq, countTq);
        when(root.get("name")).thenReturn(pathName);


        Page<Game> result = gameRepository.findGames(filter, pageable);


        assertEquals(games, result.getContent());
        assertEquals(totalRecords, result.getTotalElements());
        verify(cb, times(1)).asc(pathName);
    }

    private void setUpMocks(Pageable pageable,
                            List<Game> games,
                            Long totalRecords,
                            CriteriaBuilder cb,
                            Root<Game> root,
                            Join<Game, GameInShop> join,
                            Predicate predicatePriceSort,
                            Subquery<BigDecimal> subquery,
                            CriteriaQuery<Game> cq,
                            CriteriaQuery<Long> countCq,
                            Path pathBigDecimal,
                            Root<GameInShop> shopRoot,
                            TypedQuery<Game> tq,
                            TypedQuery<Long> countTq) {

        Expression<Long> expression = mock(Expression.class);


        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Game.class)).thenReturn(cq);
        when(cq.from(Game.class)).thenReturn(root);

        when(root.join(anyString(), any(JoinType.class))).thenAnswer(invocation -> join);
        when(predicateBuilder.buildFilters(eq(cb), eq(root), eq(join), any())).thenReturn(List.of());
        when(entityManager.createQuery(cq)).thenReturn(tq);
        when(tq.setFirstResult((int) pageable.getOffset())).thenReturn(tq);
        when(tq.setMaxResults(pageable.getPageSize())).thenReturn(tq);
        when(tq.getResultList()).thenReturn(Collections.emptyList());

        when(cb.createQuery(Long.class)).thenReturn(countCq);
        when(countCq.from(Game.class)).thenReturn(root);
        when(cb.countDistinct(root)).thenReturn(expression);
        when(countCq.select(expression)).thenReturn(countCq);
        when(entityManager.createQuery(countCq)).thenReturn(countTq);
        when(countTq.getSingleResult()).thenReturn(0L);

        if (predicatePriceSort != null) {
            when(cq.subquery(BigDecimal.class)).thenReturn(subquery);
            when(subquery.from(GameInShop.class)).thenReturn(shopRoot);

            when(shopRoot.get("game")).thenReturn(pathBigDecimal);
            when(cb.equal(pathBigDecimal, root)).thenReturn(predicatePriceSort);
        }

        when(tq.getResultList()).thenReturn(games);
        when(countTq.getSingleResult()).thenReturn(totalRecords);
    }
}

