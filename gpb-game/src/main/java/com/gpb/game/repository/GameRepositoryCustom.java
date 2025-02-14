package com.gpb.game.repository;

import com.gpb.game.entity.game.Game;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Custom repository interface for performing full-text search operations on {@link Game} entities.
 * <p>
 * This interface declares methods to perform full-text search queries and count the number of matching
 * {@code Game} entities using Hibernate Search.
 * </p>
 *
 * @see com.gpb.game.entity.game.Game
 */
public interface GameRepositoryCustom {

    /**
     * Searches for {@link Game} entities that match the given search term in their name using full-text search.
     * <p>
     * This method leverages Hibernate Search to perform a more flexible search on the {@code name} field,
     * supporting features such as tokenization, stemming, and fuzzy matching, beyond simple substring matches.
     * </p>
     *
     * @param name     the search term for the Game name.
     * @param pageable pagination information, such as page number and page size.
     * @return a {@link List} of {@code Game} entities that match the search criteria.
     */
    List<Game> searchByNameFullText(String name, Pageable pageable);

    /**
     * Counts the number of {@link Game} entities that match the given search term in their name using full-text search.
     *
     * @param name the search term for the Game name.
     * @return the total count of matching {@code Game} entities.
     */
    long countByNameFullText(String name);
}
