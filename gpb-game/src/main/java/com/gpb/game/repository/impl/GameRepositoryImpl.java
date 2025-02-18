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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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

        long totalRecords = result.total().hitCount();
        return new PageImpl<>(result.hits(), pageable, totalRecords);
    }

    @Override
    public Page<Game> findGamesByGenreAndTypeWithSorting(
            List<Genre> genres,
            List<ProductType> types,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Build main query
        CriteriaQuery<Game> query = cb.createQuery(Game.class);
        Root<Game> gameRoot = query.from(Game.class);
        Join<Game, GameInShop> gameShopJoin = gameRoot.join("gamesInShop", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        addFilters(predicates, cb, gameRoot, gameShopJoin, genres, types, minPrice, maxPrice);
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        setOrder(query, cb, gameRoot, pageable);

        // Build count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Game> countRoot = countQuery.from(Game.class);
        Join<Game, GameInShop> countShopJoin = countRoot.join("gamesInShop", JoinType.LEFT);
        List<Predicate> countPredicates = new ArrayList<>();
        addFilters(countPredicates, cb, countRoot, countShopJoin, genres, types, minPrice, maxPrice);
        countQuery.select(cb.countDistinct(countRoot))
                .where(cb.and(countPredicates.toArray(new Predicate[0])));

        return getPageResult(query, countQuery, pageable);
    }

    @Override
    public Page<Game> findGamesByUserWithSorting(Long userId, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Build main query
        CriteriaQuery<Game> query = cb.createQuery(Game.class);
        Root<Game> gameRoot = query.from(Game.class);
        Join<Game, BasicUser> userJoin = gameRoot.join("userList", JoinType.INNER);
        query.where(cb.equal(userJoin.get("id"), userId));
        setOrder(query, cb, gameRoot, pageable);

        // Build count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Game> countRoot = countQuery.from(Game.class);
        Join<Game, BasicUser> countUserJoin = countRoot.join("userList", JoinType.INNER);
        countQuery.select(cb.countDistinct(countRoot))
                .where(cb.equal(countUserJoin.get("id"), userId));

        return getPageResult(query, countQuery, pageable);
    }

    /**
     * Helper to add filters to both main and count queries.
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
            predicates.add(cb.between(gameShopJoin.get("discountPrice"), minPrice, maxPrice));
        }
    }

    /**
     * Helper to apply ordering to the query.
     */
    private void setOrder(CriteriaQuery<Game> query, CriteriaBuilder cb, Root<Game> gameRoot, Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();
                Order ordering;
                if ("gamesInShop.discountPrice".equals(property)) {
                    // Use a subquery for sorting on discountPrice
                    Subquery<BigDecimal> subquery = query.subquery(BigDecimal.class);
                    Root<GameInShop> shopRoot = subquery.from(GameInShop.class);
                    subquery.select(order.isAscending()
                            ? cb.min(shopRoot.get("discountPrice"))
                            : cb.max(shopRoot.get("discountPrice")));
                    subquery.where(cb.equal(shopRoot.get("game"), gameRoot));
                    ordering = order.isAscending() ? cb.asc(subquery) : cb.desc(subquery);
                } else {
                    ordering = order.isAscending()
                            ? cb.asc(gameRoot.get(property))
                            : cb.desc(gameRoot.get(property));
                }
                orders.add(ordering);
            }
            query.orderBy(orders);
        }
    }

    /**
     * Helper method to execute both the main and count queries and return a paged result.
     */
    private Page<Game> getPageResult(CriteriaQuery<Game> query, CriteriaQuery<Long> countQuery, Pageable pageable) {
        TypedQuery<Game> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Game> resultList = typedQuery.getResultList();

        Long totalRecords = entityManager.createQuery(countQuery).getSingleResult();
        return new PageImpl<>(resultList, pageable, totalRecords);
    }
}
