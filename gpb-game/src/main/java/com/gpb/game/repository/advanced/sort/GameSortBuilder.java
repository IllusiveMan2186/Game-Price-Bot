package com.gpb.game.repository.advanced.sort;

import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.util.Constants;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility component for applying dynamic sorting to JPA Criteria queries involving {@link Game} entities.
 * <p>
 * Supports basic field sorting and special handling for aggregated discount prices from the {@link GameInShop} relation.
 */
@Component
public class GameSortBuilder {

    private static final String DISCOUNT_PRICE_PROPERTY = "gamesInShop.discountPrice";

    /**
     * Applies sorting to the given CriteriaQuery based on the Sort information from {@link Pageable}.
     *
     * @param query    the CriteriaQuery to apply sorting to
     * @param cb       the CriteriaBuilder used to construct Order expressions
     * @param gameRoot the root entity in the query (i.e., {@link Game})
     * @param pageable the Pageable containing sort information
     */
    public void applySorting(CriteriaQuery<Game> query, CriteriaBuilder cb, Root<Game> gameRoot, Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            return;
        }

        List<Order> orders = new ArrayList<>();
        for (Sort.Order sortOrder : pageable.getSort()) {
            orders.add(createOrderForProperty(cb, gameRoot, sortOrder));
        }
        query.orderBy(orders);
    }

    /**
     * Creates a single {@link Order} object for a given property, including special handling for nested fields.
     *
     * @param cb        the CriteriaBuilder
     * @param gameRoot  the root of the query
     * @param sortOrder the sort directive (property and direction)
     * @return an {@link Order} object to be used in the query
     */
    private Order createOrderForProperty(CriteriaBuilder cb,
                                         Root<Game> gameRoot,
                                         Sort.Order sortOrder) {
        String property = sortOrder.getProperty();

        if (DISCOUNT_PRICE_PROPERTY.equals(property)) {
            return sortOrder.isAscending()
                    ? cb.asc(gameRoot.get(Constants.MIN_DISCOUNT_PRICE_FORMULA_FIELD))
                    : cb.desc(gameRoot.get(Constants.MAX_DISCOUNT_PRICE_FORMULA_FIELD));
        }

        return sortOrder.isAscending()
                ? cb.asc(gameRoot.get(property))
                : cb.desc(gameRoot.get(property));
    }
}
