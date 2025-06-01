package com.gpb.game.repository.impl;

import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameRepositorySearchFilter;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.repository.GameRepositoryCustom;
import com.gpb.game.repository.predicate.GamePredicateBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class GameRepositoryCustomImpl implements GameRepositoryCustom {

    private static final String DISCOUNT_PRICE = "discountPrice";
    private static final String GAME_IN_SHOP = "gamesInShop";
    private static final String GAME_IN_SHOP_DISCOUNT_PRICE = GAME_IN_SHOP + "." + DISCOUNT_PRICE;

    @PersistenceContext
    private EntityManager entityManager;

    private GamePredicateBuilder predicateBuilder;

    public GameRepositoryCustomImpl(GamePredicateBuilder predicateBuilder) {
        this.predicateBuilder = predicateBuilder;
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
        CriteriaQuery<Game> query = getSelectGamesCriteriaQuery(cb, filter, pageable);
        CriteriaQuery<Long> countQuery = getCountCriteriaQuery(cb, filter);

        return getPageResult(query, countQuery, pageable);
    }

    private CriteriaQuery<Game> getSelectGamesCriteriaQuery(CriteriaBuilder cb, GameRepositorySearchFilter filter, Pageable pageable) {
        CriteriaQuery<Game> query = cb.createQuery(Game.class);
        Root<Game> root = query.from(Game.class);
        Join<Game, GameInShop> shopJoin = root.join(GAME_IN_SHOP, JoinType.LEFT);

        List<Predicate> predicates = predicateBuilder.buildFilters(cb, root, shopJoin, filter);
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        applySorting(query, cb, root, pageable);

        return query;
    }

    private CriteriaQuery<Long> getCountCriteriaQuery(CriteriaBuilder cb, GameRepositorySearchFilter filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Game> countRoot = countQuery.from(Game.class);
        Join<Game, GameInShop> countShopJoin = countRoot.join(GAME_IN_SHOP, JoinType.LEFT);
        List<Predicate> countPredicates = predicateBuilder.buildFilters(cb, countRoot, countShopJoin, filter);
        countQuery.select(cb.countDistinct(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));

        return countQuery;
    }

    /**
     * Applies sorting to the query based on the pageable sort.
     */
    private void applySorting(CriteriaQuery<Game> query, CriteriaBuilder cb, Root<Game> gameRoot, Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            return;
        }
        List<Order> orders = new ArrayList<>();
        for (Sort.Order sortOrder : pageable.getSort()) {
            orders.add(createOrderForProperty(query, cb, gameRoot, sortOrder));
        }
        query.orderBy(orders);
    }

    private Order createOrderForProperty(CriteriaQuery<Game> query, CriteriaBuilder cb, Root<Game> gameRoot,
                                         Sort.Order sortOrder) {
        final String property = sortOrder.getProperty();
        if (GAME_IN_SHOP_DISCOUNT_PRICE.equals(property)) {
            return createDiscountPriceOrder(query, cb, gameRoot, sortOrder);
        }
        return sortOrder.isAscending()
                ? cb.asc(gameRoot.get(property))
                : cb.desc(gameRoot.get(property));
    }

    private Order createDiscountPriceOrder(CriteriaQuery<Game> query, CriteriaBuilder cb, Root<Game> gameRoot,
                                           Sort.Order sortOrder) {
        Subquery<BigDecimal> subquery = query.subquery(BigDecimal.class);
        Root<GameInShop> shopRoot = subquery.from(GameInShop.class);

        Expression<BigDecimal> discountPriceExpr = shopRoot.get(DISCOUNT_PRICE);
        Expression<BigDecimal> aggregated = sortOrder.isAscending()
                ? cb.min(discountPriceExpr)
                : cb.max(discountPriceExpr);
        subquery.select(aggregated);
        subquery.where(cb.equal(shopRoot.get("game"), gameRoot));
        return sortOrder.isAscending()
                ? cb.asc(subquery)
                : cb.desc(subquery);
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