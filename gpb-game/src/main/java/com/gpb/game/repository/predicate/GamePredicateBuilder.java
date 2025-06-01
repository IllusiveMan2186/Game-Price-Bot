package com.gpb.game.repository.predicate;

import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameRepositorySearchFilter;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Package for building dynamic query predicates used in {@link com.gpb.game.repository.GameRepositoryCustom}
 * implementations.
 * <p>
 * This package contains predicate builders with filtering logic
 * for complex query construction using the JPA Criteria API.
 * </p>
 */
@Component
public class GamePredicateBuilder {

    /**
     * Constructs a list of conditions for filtering games based on the provided {@link GameRepositorySearchFilter}.
     *
     * @param cb        the {@link CriteriaBuilder} used to build criteria predicates
     * @param root      the root entity of type {@link Game}
     * @param shopJoin  a left join to {@link GameInShop}, used for filtering by discountPrice
     * @param filter    the filtering criteria encapsulated in a {@link GameRepositorySearchFilter} object
     * @return a list of {@link Predicate} conditions to be applied in a query
     */
    public List<Predicate> buildFilters(CriteriaBuilder cb,
                                        Root<Game> root,
                                        Join<Game, GameInShop> shopJoin,
                                        GameRepositorySearchFilter filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getGenres() != null && !filter.getGenres().isEmpty()) {
            predicates.add(root.join("genres", JoinType.INNER).in(filter.getGenres()));
        }

        if (filter.getTypes() != null && !filter.getTypes().isEmpty()) {
            predicates.add(root.get("type").in(filter.getTypes()));
        }

        if (filter.getMinPrice() != null && filter.getMaxPrice() != null) {
            predicates.add(cb.between(shopJoin.get("discountPrice"), filter.getMinPrice(), filter.getMaxPrice()));
        }

        if (filter.getUserId() != null) {
            Join<Game, BasicUser> userJoin = root.join("userList", JoinType.INNER);
            predicates.add(cb.equal(userJoin.get("id"), filter.getUserId()));
        }

        return predicates;
    }
}

