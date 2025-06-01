package com.gpb.game.repository.advanced.impl;

import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.game.GameRepositorySearchFilter;
import com.gpb.game.repository.advanced.GameRepositoryAdvance;
import com.gpb.game.repository.advanced.predicate.GameFilterPredicateBuilder;
import com.gpb.game.repository.advanced.sort.GameSortBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class GameRepositoryAdvancedImpl implements GameRepositoryAdvance {

    private static final String GAME_IN_SHOP = "gamesInShop";

    @PersistenceContext
    private EntityManager entityManager;

    private final GameFilterPredicateBuilder predicateBuilder;
    private final GameSortBuilder gameSortBuilder;

    public GameRepositoryAdvancedImpl(GameFilterPredicateBuilder predicateBuilder, GameSortBuilder gameSortBuilder) {
        this.predicateBuilder = predicateBuilder;
        this.gameSortBuilder = gameSortBuilder;
    }

    @Override
    public Page<Game> searchByNameFullText(String name, Pageable pageable) {
        log.debug("Search for game with name similar to '{}'", name);
        SearchResult<Game> result = Search.session(entityManager)
                .search(Game.class)
                .where(f -> f.wildcard()
                        .field("name")
                        .matching("*" + name.toLowerCase() + "*"))
                .fetch((int) pageable.getOffset(), pageable.getPageSize());

        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }


    @Override
    public Page<Game> findGames(GameRepositorySearchFilter filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Game> query = buildSelectQuery(cb, filter, pageable);
        CriteriaQuery<Long> countQuery = buildCountQuery(cb, filter);

        return getPageResult(query, countQuery, pageable);
    }

    private CriteriaQuery<Game> buildSelectQuery(CriteriaBuilder cb,
                                                 GameRepositorySearchFilter filter,
                                                 Pageable pageable) {
        CriteriaQuery<Game> query = cb.createQuery(Game.class);
        Root<Game> root = query.from(Game.class);
        Join<Game, GameInShop> shopJoin = root.join(GAME_IN_SHOP, JoinType.LEFT);

        List<Predicate> predicates = predicateBuilder.buildFilters(cb, root, shopJoin, filter);
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        gameSortBuilder.applySorting(query, cb, root, pageable);

        return query;
    }

    private CriteriaQuery<Long> buildCountQuery(CriteriaBuilder cb, GameRepositorySearchFilter filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Game> countRoot = countQuery.from(Game.class);
        Join<Game, GameInShop> countShopJoin = countRoot.join(GAME_IN_SHOP, JoinType.LEFT);
        List<Predicate> countPredicates = predicateBuilder.buildFilters(cb, countRoot, countShopJoin, filter);
        countQuery.select(cb.countDistinct(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));

        return countQuery;
    }

    /**
     * Executes the main and count queries and returns a paginated result.
     */
    private Page<Game> getPageResult(CriteriaQuery<Game> mainQuery, CriteriaQuery<Long> countQuery, Pageable pageable) {
        final TypedQuery<Game> typedQuery = entityManager.createQuery(mainQuery);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        final List<Game> games = typedQuery.getResultList();

        final Long totalRecords = entityManager.createQuery(countQuery).getSingleResult();
        return new PageImpl<>(games, pageable, totalRecords);
    }

}