package com.gpb.game.repository;

import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameRepositorySearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for performing advanced queries on {@link Game} entities.
 * <p>
 * This interface defines methods for performing full-text search operations, retrieving games by user,
 * and filtering games by genres, types, and price with dynamic sorting.
 * </p>
 *
 * @see com.gpb.game.entity.game.Game
 */
public interface GameRepositoryCustom {

    /**
     * Performs a full-text search on the {@link Game} entity's name field.
     * <p>
     * This method supports advanced search features providing more flexible search results compared
     * to simple substring matching.
     * </p>
     *
     * @param name     The search term to match against the game names.
     * @param pageable The pagination and sorting information.
     * @return A {@link Page} of {@code Game} entities that match the search criteria.
     */
    Page<Game> searchByNameFullText(String name, Pageable pageable);

    /**
     * Retrieves a paginated and filtered list of {@link Game} entities based on the provided filter criteria.
     * <p>
     * Supports dynamic filtering by genre, product type, price range, and associated user,
     * as well as sorting by any supported property (including joined field discount price).
     * </p>
     *
     * @param filter   A {@link GameRepositorySearchFilter} object containing optional filtering parameters
     *                 such as genres, types, min/max price, and user ID.
     * @param pageable The pagination and sorting details.
     * @return A {@link Page} of games that match the given filtering and sorting criteria.
     */
    Page<Game> findGames(GameRepositorySearchFilter filter, Pageable pageable);
}
