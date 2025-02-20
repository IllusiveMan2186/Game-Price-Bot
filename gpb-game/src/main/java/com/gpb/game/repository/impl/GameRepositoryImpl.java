package com.gpb.game.repository.impl;

import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.repository.GameRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GameRepositoryImpl implements GameRepositoryCustom {

    private static final String DISCOUNT_PRICE = "discountPrice";
    private static final String GAME_IN_SHOP = "gamesInShop";
    private static final String USER_LIST = "userList";


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Game> searchByNameFullText(String name, Pageable pageable) {
        SearchResult<Game> result = Search.session(entityManager)
                .search(Game.class)
                .where(f -> f.match()
                        .fields("name")
                        .matching("*" + name + "*"))
                .fetch((int) pageable.getOffset(), pageable.getPageSize());

        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    @Override
    public Page<Game> findGamesByGenreAndTypeWithSorting(List<Genre> genres,
                                                         List<ProductType> types,
                                                         BigDecimal minPrice,
                                                         BigDecimal maxPrice,
                                                         Pageable pageable) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Build main query
        final CriteriaQuery<Game> mainQuery = cb.createQuery(Game.class);
        final Root<Game> gameRoot = mainQuery.from(Game.class);
        final Join<Game, GameInShop> gameShopJoin = gameRoot.join(GAME_IN_SHOP, JoinType.LEFT);

        final List<Predicate> predicates = new ArrayList<>();
        addFilters(predicates, cb, gameRoot, gameShopJoin, genres, types, minPrice, maxPrice);
        mainQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        applySorting(mainQuery, cb, gameRoot, pageable);

        // Build count query
        final CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        final Root<Game> countRoot = countQuery.from(Game.class);
        final Join<Game, GameInShop> countShopJoin = countRoot.join(GAME_IN_SHOP, JoinType.LEFT);
        final List<Predicate> countPredicates = new ArrayList<>();
        addFilters(countPredicates, cb, countRoot, countShopJoin, genres, types, minPrice, maxPrice);
        countQuery.select(cb.countDistinct(countRoot))
                .where(cb.and(countPredicates.toArray(new Predicate[0])));

        return getPageResult(mainQuery, countQuery, pageable);
    }

    @Override
    public Page<Game> findGamesByUserWithSorting(Long userId, Pageable pageable) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Build main query
        final CriteriaQuery<Game> mainQuery = cb.createQuery(Game.class);
        final Root<Game> gameRoot = mainQuery.from(Game.class);
        final Join<Game, BasicUser> userJoin = gameRoot.join(USER_LIST, JoinType.INNER);
        mainQuery.where(cb.equal(userJoin.get("id"), userId));
        applySorting(mainQuery, cb, gameRoot, pageable);

        // Build count query
        final CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        final Root<Game> countRoot = countQuery.from(Game.class);
        final Join<Game, BasicUser> countUserJoin = countRoot.join(USER_LIST, JoinType.INNER);
        countQuery.select(cb.countDistinct(countRoot))
                .where(cb.equal(countUserJoin.get("id"), userId));

        return getPageResult(mainQuery, countQuery, pageable);
    }

    /**
     * Adds filtering predicates to the query.
     */
    private void addFilters(List<Predicate> predicates, CriteriaBuilder cb, Root<Game> gameRoot,
                            Join<Game, GameInShop> gameShopJoin, List<Genre> genres,
                            List<ProductType> types, BigDecimal minPrice, BigDecimal maxPrice) {
        if (genres != null && !genres.isEmpty()) {
            Join<Game, Genre> genreJoin = gameRoot.join("genres", JoinType.INNER);
            predicates.add(genreJoin.in(genres));
        }
        if (types != null && !types.isEmpty()) {
            predicates.add(gameRoot.get("type").in(types));
        }
        if (minPrice != null && maxPrice != null) {
            predicates.add(cb.between(gameShopJoin.get(DISCOUNT_PRICE), minPrice, maxPrice));
        }
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
        if ("gamesInShop.discountPrice".equals(property)) {
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
        // Assume DISCOUNT_PRICE is a constant defined as "discountPrice"
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
