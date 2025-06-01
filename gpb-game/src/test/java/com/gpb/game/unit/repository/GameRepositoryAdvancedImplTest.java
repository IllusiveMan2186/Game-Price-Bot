package com.gpb.game.unit.repository;

import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.game.GameRepositorySearchFilter;
import com.gpb.game.repository.advanced.impl.GameRepositoryAdvancedImpl;
import com.gpb.game.repository.advanced.predicate.GameFilterPredicateBuilder;
import com.gpb.game.repository.advanced.sort.GameSortBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameRepositoryAdvancedImplTest {

    private CriteriaBuilder cb;
    private Root<Game> root;
    private CriteriaQuery<Game> cq;
    private Join<Game, GameInShop> join;
    private TypedQuery<Game> tq;
    private CriteriaQuery<Long> countCq;
    private TypedQuery<Long> countTq;

    @Mock
    private EntityManager entityManager;

    @Mock
    private GameFilterPredicateBuilder predicateBuilder;
    @Mock
    private GameSortBuilder gameSortBuilder;

    private GameRepositoryAdvancedImpl gameRepository;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);
        gameRepository = new GameRepositoryAdvancedImpl(predicateBuilder, gameSortBuilder);

        Field entityManagerField = GameRepositoryAdvancedImpl.class.getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(gameRepository, entityManager);

        cb = mock(CriteriaBuilder.class);
        root = mock(Root.class);
        cq = mock(CriteriaQuery.class);
        join = mock(Join.class);
        cq = mock(CriteriaQuery.class);
        countCq = mock(CriteriaQuery.class);
        tq = mock(TypedQuery.class);
        countTq = mock(TypedQuery.class);
    }

    @Test
    void testFindGames_whenGamesNotFound_shouldCallApplySortAndReturnEmptyList() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder().build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "gamesInShop.discountPrice"));

        List<Game> games = Collections.EMPTY_LIST;
        Long totalRecords = 0L;

        setUpMocks(pageable, games, totalRecords, cb, root, join, cq, countCq, tq, countTq);

        Page<Game> result = gameRepository.findGames(filter, pageable);


        assertEquals(games, result.getContent());
        assertEquals(totalRecords, result.getTotalElements());
        verify(gameSortBuilder, times(1)).applySorting(cq, cb, root, pageable);
    }

    @Test
    void testFindGames_whenGamesFound_shouldCallApplySortAndReturnPageWithGames() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder().build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "gamesInShop.discountPrice"));

        List<Game> games = List.of(new Game(), new Game());
        Long totalRecords = 2L;

        setUpMocks(pageable, games, totalRecords, cb, root, join, cq, countCq, tq, countTq);

        Page<Game> result = gameRepository.findGames(filter, pageable);


        assertEquals(games, result.getContent());
        assertEquals(totalRecords, result.getTotalElements());
        verify(gameSortBuilder, times(1)).applySorting(cq, cb, root, pageable);
    }

    private void setUpMocks(Pageable pageable,
                            List<Game> games,
                            Long totalRecords,
                            CriteriaBuilder cb,
                            Root<Game> root,
                            Join<Game, GameInShop> join,
                            CriteriaQuery<Game> cq,
                            CriteriaQuery<Long> countCq,
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

        when(tq.getResultList()).thenReturn(games);
        when(countTq.getSingleResult()).thenReturn(totalRecords);
    }
}

