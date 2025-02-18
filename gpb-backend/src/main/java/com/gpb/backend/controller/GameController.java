package com.gpb.backend.controller;

import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.GameService;
import com.gpb.backend.service.ResourceService;
import com.gpb.common.entity.game.AddGameInStoreDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.exception.PriceRangeException;
import com.gpb.common.util.CommonConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for handling game-related endpoints.ges.
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final ResourceService resourceService;

    public GameController(final GameService gameService, final ResourceService resourceService) {
        this.gameService = gameService;
        this.resourceService = resourceService;
    }

    /**
     * Retrieves game information by its ID.
     *
     * @param gameId the game ID
     * @param user   the current user (can be {@code null} for unauthenticated requests)
     * @return the game information DTO
     */
    @GetMapping(value = "/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public GameInfoDto getGameById(
            @PathVariable
            @Positive final long gameId,

            @AuthenticationPrincipal final UserDto user
    ) {
        final long basicUserId = (user == null) ? -1 : user.getBasicUserId();
        log.info("Retrieving game by ID {} for basic user {}", gameId, basicUserId);
        return gameService.getById(gameId, basicUserId);
    }

    /**
     * Retrieves a paginated list of games by their name.
     *
     * @param name     the game name to search for
     * @param pageSize the number of games per page (default is 25)
     * @param pageNum  the page number to retrieve (default is 1)
     * @param sortBy   the sort parameter (default is "gamesInShop.discountPrice-ASC")
     * @return a paginated list of games matching the name
     */
    @GetMapping(value = "/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public GameListPageDto getGameByName(
            @PathVariable
            @Pattern(regexp = CommonConstants.NAME_REGEX_PATTERN) final String name,

            @RequestParam(required = false, defaultValue = "25")
            @Min(value = 1) final int pageSize,

            @RequestParam(required = false, defaultValue = "1")
            @Min(value = 1) final int pageNum,

            @RequestParam(required = false, defaultValue = "gamesInShop.discountPrice-ASC")
            @Pattern(regexp = CommonConstants.SORT_PARAM_REGEX) final String sortBy
    ) {
        log.info("Searching for game by name '{}'", name);
        return gameService.getByName(name, pageSize, pageNum, sortBy);
    }

    /**
     * Retrieves game information by its store URL.
     *
     * @param url the game URL from the store
     * @return the game information DTO
     */
    @GetMapping(value = "/url")
    @ResponseStatus(HttpStatus.OK)
    public GameInfoDto getGameByUrl(
            @RequestParam
            @NotBlank
            @Pattern(regexp = CommonConstants.URL_REGEX_PATTERN) final String url
    ) {
        log.info("Retrieving game by URL '{}'", url);
        return gameService.getByUrl(url);
    }

    /**
     * Processes a game in store add event from url to registered game .
     * <p>
     * This method add the game in store by url to game by the given ID
     * </p>
     *
     * @param addGameInStoreDto the dto containing the game ID to which game in store
     *                          should be added and game in store url.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/url")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addGameInStoreToCreatedGameByUrl(@RequestBody final AddGameInStoreDto addGameInStoreDto) {
        log.info("Add to game {} game from store by URL '{}'", addGameInStoreDto.getGameId(), addGameInStoreDto.getUrl());
        gameService.addGameInStore(addGameInStoreDto);
    }

    /**
     * Retrieves a paginated list of games filtered by genre, product type, price range, and sort order.
     *
     * @param genre    a list of genres to filter by (optional)
     * @param type     a list of product types to exclude (optional)
     * @param pageSize the number of games per page (default is 25)
     * @param pageNum  the page number to retrieve (default is 1)
     * @param minPrice the minimal price (default is 0)
     * @param maxPrice the maximal price (default is 10000)
     * @param sortBy   the sort parameter (default is "gamesInShop.discountPrice-ASC")
     * @return a paginated list of games matching the filters
     * @throws PriceRangeException if the price range is invalid (maxPrice < minPrice)
     */
    @GetMapping(value = "/genre")
    @ResponseStatus(HttpStatus.OK)
    public GameListPageDto getGamesForGenre(
            @RequestParam(required = false) final List<Genre> genre,
            @RequestParam(required = false) final List<ProductType> type,

            @RequestParam(required = false, defaultValue = "25")
            @Positive final int pageSize,

            @RequestParam(required = false, defaultValue = "1")
            @Positive final int pageNum,

            @RequestParam(required = false, defaultValue = "0")
            @Min(value = 0) final BigDecimal minPrice,

            @RequestParam(required = false, defaultValue = "10000")
            @Min(value = 0) final BigDecimal maxPrice,

            @RequestParam(required = false, defaultValue = "gamesInShop.discountPrice-ASC")
            @Pattern(regexp = CommonConstants.SORT_PARAM_REGEX) final String sortBy
    ) {
        log.info("Retrieving games for genres: {} with exclusion types: {} and price range {} - {}; pageSize={}, pageNum={}, sortBy={}",
                genre, type, minPrice, maxPrice, pageSize, pageNum, sortBy);
        if (maxPrice.compareTo(minPrice) < 0) {
            log.info("Invalid price range: {} - {}", minPrice, maxPrice);
            throw new PriceRangeException();
        }
        return gameService.getByGenre(genre, type, pageSize, pageNum, minPrice, maxPrice, sortBy);
    }

    /**
     * Retrieves a paginated list of games to which user is subscribed.
     *
     * @param pageSize the number of games per page (default is 25)
     * @param pageNum  the page number to retrieve (default is 1)
     * @param sortBy   the sort parameter (default is "gamesInShop.discountPrice-ASC")
     * @param user     the authenticated user's details
     * @return a paginated list of the user's games
     */
    @GetMapping(value = "/user/games")
    @ResponseStatus(HttpStatus.OK)
    public GameListPageDto getGamesOfUser(
            @RequestParam(required = false, defaultValue = "25")
            @Positive final int pageSize,

            @RequestParam(required = false, defaultValue = "1")
            @Positive final int pageNum,

            @RequestParam(required = false, defaultValue = "gamesInShop.discountPrice-ASC")
            @Pattern(regexp = CommonConstants.SORT_PARAM_REGEX) final String sortBy,

            @AuthenticationPrincipal final UserDto user
    ) {
        log.info("Retrieving games for user {} with pageSize={}, pageNum={}, sortBy={}",
                user.getId(), pageSize, pageNum, sortBy);
        return gameService.getUserGames(user.getBasicUserId(), pageSize, pageNum, sortBy);
    }

    /**
     * Removes a game from the database by its ID.
     *
     * @param gameId the ID of the game to remove
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGameById(
            @PathVariable
            @Positive final long gameId
    ) {
        log.info("Removing game with ID {} from database", gameId);
        gameService.removeGame(gameId);
        log.info("Game with ID {} successfully removed", gameId);
    }

    /**
     * Removes a game from the store by its store ID.
     * <p>This operation is restricted to administrators.</p>
     *
     * @param gameInStoreId the store-specific game ID to remove
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/store/{gameInStoreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGameInStoreById(
            @PathVariable
            @Positive final long gameInStoreId
    ) {
        log.info("Removing game in store with ID {} from database", gameInStoreId);
        gameService.removeGameInStore(gameInStoreId);
        log.info("Game in store with ID {} successfully removed", gameInStoreId);
    }

    /**
     * Retrieves the image of a game by its name.
     *
     * @param gameName the name of the game
     * @return a byte array representing the game image
     */
    @GetMapping(value = "/image/{gameName}")
    @ResponseStatus(HttpStatus.OK)
    public byte[] getGameImage(
            @PathVariable
            @NotBlank
            @Pattern(regexp = CommonConstants.NAME_REGEX_PATTERN) final String gameName
    ) {
        log.info("Retrieving image for game '{}'", gameName);
        return resourceService.getGameImage(gameName);
    }
}