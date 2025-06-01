package com.gpb.game.repository.advanced.sort;

import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility component for applying dynamic sorting to JPA Criteria queries involving {@link Game} entities.
 * <p>
 * Supports basic field sorting and special handling for aggregated discount prices from the {@link GameInShop} relation.
 */
@Component
public class GameSortBuilder {

    private static final String DISCOUNT_PRICE = "discountPrice";
    private static final String GAME_IN_SHOP = "gamesInShop";
    private static final String GAME_IN_SHOP_DISCOUNT_PRICE = GAME_IN_SHOP + "." + DISCOUNT_PRICE;

    /**
     * Applies sorting to the given CriteriaQuery based on the Sort information from {@link Pageable}.
     *
     * @param query    the CriteriaQuery to apply sorting to
     * @param cb       the CriteriaBuilder used to construct Order expressions
     * @param gameRoot the root entity in the query (i.e., {@link Game})
     * @param pageable the Pageable containing sort information
     */
    public void applySorting(CriteriaQuery<Game> query, CriteriaBuilder cb, Root<Game> gameRoot, Pageable pageable) {
        if (!pageable.getSort().isSorted()) return;

        List<Order> orders = new ArrayList<>();
        for (Sort.Order sortOrder : pageable.getSort()) {
            orders.add(createOrderForProperty(query, cb, gameRoot, sortOrder));
        }

        query.orderBy(orders);
    }

    /**
     * Creates a single {@link Order} object for a given property, including special handling for nested fields.
     *
     * @param query     the CriteriaQuery being built
     * @param cb        the CriteriaBuilder
     * @param gameRoot  the root of the query
     * @param sortOrder the sort directive (property and direction)
     * @return an {@link Order} object to be used in the query
     */
    private Order createOrderForProperty(CriteriaQuery<Game> query,
                                         CriteriaBuilder cb,
                                         Root<Game> gameRoot,
                                         Sort.Order sortOrder) {
        String property = sortOrder.getProperty();

        if (GAME_IN_SHOP_DISCOUNT_PRICE.equals(property)) {
            return createDiscountPriceOrder(query, cb, gameRoot, sortOrder);
        }

        return sortOrder.isAscending()
                ? cb.asc(gameRoot.get(property))
                : cb.desc(gameRoot.get(property));
    }

    /**
     * Creates an {@link Order} object for the aggregated discount price (min/max) from related {@link GameInShop} entities.
     *
     * @param query     the CriteriaQuery being built
     * @param cb        the CriteriaBuilder
     * @param gameRoot  the root of the main query
     * @param sortOrder the sort directive
     * @return an {@link Order} object for sorting by aggregated discount price
     */
    private Order createDiscountPriceOrder(CriteriaQuery<Game> query,
                                           CriteriaBuilder cb,
                                           Root<Game> gameRoot,
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
}
