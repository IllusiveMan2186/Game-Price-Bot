package com.gpb.game.repository;

import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.entity.game.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

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
     * Retrieves a paginated list of {@link Game} entities associated with a specific user.
     * <p>
     * This method fetches games that are linked to a particular user, allowing for dynamic sorting
     * based on various attributes such as name, price, and other game details.
     * </p>
     *
     * @param userId   The unique identifier of the user whose games should be retrieved.
     * @param pageable The pagination and sorting details.
     * @return A {@link Page} of games belonging to the specified user.
     */
    Page<Game> findGamesByUserWithSorting(Long userId, Pageable pageable);

    /**
     * Retrieves a paginated list of {@link Game} entities filtered by genre, type, and price range,
     * with support for dynamic sorting.
     * <p>
     * This method allows filtering games based on multiple criteria:
     * <ul>
     *     <li>Genre: The type of game, such as "RPG" or "Action".</li>
     *     <li>Product Type: The category of the game, such as "GAME" or "CURRENCY".</li>
     *     <li>Discount Price Range: The minimum and maximum price of the game.</li>
     * </ul>
     * Additionally, results can be dynamically sorted based on various attributes.
     * </p>
     *
     * @param genres   A list of {@link Genre} categories to filter the games.
     * @param types    A list of {@link ProductType} categories to filter the games.
     * @param minPrice The minimum discount price for filtering games.
     * @param maxPrice The maximum discount price for filtering games.
     * @param pageable The pagination and sorting details.
     * @return A {@link Page} containing games that match the provided filters.
     */
    Page<Game> findGamesByGenreAndTypeWithSorting(
            List<Genre> genres,
            List<ProductType> types,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );
}
