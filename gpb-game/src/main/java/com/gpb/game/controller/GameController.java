package com.gpb.game.controller;

import com.gpb.common.entity.game.GameDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.exception.PriceRangeException;
import com.gpb.common.exception.SortParamException;
import com.gpb.common.util.CommonConstants;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.service.GameInShopService;
import com.gpb.game.service.GameService;
import com.gpb.game.service.ResourceService;
import com.gpb.game.service.UserService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.index.qual.Positive;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;

/**
 * REST controller for handling game-related requests.
 * <p>
 * Provides endpoints for retrieving game details by ID, name, URL, genre, and for retrieving a user's games.
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/game")
public class GameController {

    private static final java.util.regex.Pattern SORT_PARAM_PATTERN = java.util.regex.Pattern.compile(CommonConstants.SORT_PARAM_REGEX);

    private final GameService gameService;
    private final GameInShopService gameInShopService;
    private final UserService userService;
    private final ResourceService resourceService;

    public GameController(GameService gameService, GameInShopService gameInShopService,
                          UserService userService, ResourceService resourceService) {
        this.gameService = gameService;
        this.gameInShopService = gameInShopService;
        this.userService = userService;
        this.resourceService = resourceService;
    }

    /**
     * Retrieves a game by its identifier.
     *
     * @param gameId the unique identifier of the game.
     * @param userId (optional) the identifier of the user making the request; used to mark subscription status.
     * @return a {@link GameInfoDto} containing detailed information about the game.
     */
    @GetMapping("/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public GameInfoDto getGameById(
            @PathVariable
            @Positive final long gameId,

            @RequestHeader(name = CommonConstants.BASIC_USER_ID_HEADER, required = false) final Long userId
    ) {
        log.info("Fetching game with id {} for user {}", gameId, userId);
        GameInfoDto gameInfo = gameService.getDtoById(gameId);
        return markUserSubscription(gameInfo, userId);
    }

    /**
     * Retrieves games by their name.
     *
     * @param name     the name of the game(s) to search for.
     * @param pageSize the number of elements per page.
     * @param pageNum  the page number to retrieve.
     * @param sortBy   the sort criteria in the format {@code property-DIRECTION}.
     * @param userId   (optional) the identifier of the user making the request; used to mark subscription status.
     * @return a {@link GameListPageDto} containing a list of games matching the search criteria.
     * @throws SortParamException if the sort parameter is invalid.
     */
    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public GameListPageDto getGameByName(
            @PathVariable
            @Pattern(regexp = CommonConstants.NAME_REGEX_PATTERN) final String name,

            @RequestParam
            @Min(value = 1) final int pageSize,

            @RequestParam
            @Min(value = 1) final int pageNum,

            @RequestParam
            @Pattern(regexp = CommonConstants.SORT_PARAM_REGEX) final String sortBy,

            @RequestHeader(name = CommonConstants.BASIC_USER_ID_HEADER, required = false) final Long userId
    ) {
        log.info("Searching games by name '{}' for page {} with {} items per page sorted by {}",
                name, pageNum, pageSize, sortBy);
        GameListPageDto gamePage = gameService.getByName(name, pageSize, pageNum, parseSortBy(sortBy));
        return markUserSubscription(gamePage, userId);
    }

    /**
     * Retrieves a game by its URL.
     *
     * @param url the URL of the game from the store.
     * @return a {@link GameInfoDto} containing detailed information about the game.
     */
    @GetMapping("/url")
    @ResponseStatus(HttpStatus.OK)
    public GameInfoDto getGameByUrl(
            @RequestParam
            @Pattern(regexp = CommonConstants.URL_REGEX_PATTERN) final String url
    ) {
        log.info("Fetching game by URL: {}", url);
        return gameInShopService.getByUrl(url);
    }

    /**
     * Retrieves a paginated list of games filtered by genres, product types, and price range.
     *
     * @param genres         (optional) a list of genres to include.
     * @param typesToExclude (optional) a list of product types to exclude.
     * @param pageSize       the number of elements per page.
     * @param pageNum        the page number to retrieve.
     * @param minPrice       the minimum price filter.
     * @param maxPrice       the maximum price filter.
     * @param sortBy         the sort criteria in the format {@code property-DIRECTION} (e.g., "price-DESC").
     * @param userId         (optional) the identifier of the user making the request; used to mark subscription status.
     * @return a {@link GameListPageDto} containing the filtered list of games.
     * @throws PriceRangeException if {@code maxPrice} is lower than {@code minPrice}.
     * @throws SortParamException  if the sort parameter is invalid.
     */
    @GetMapping("/genre")
    @ResponseStatus(HttpStatus.OK)
    public GameListPageDto getGamesForGenre(
            @RequestParam(required = false) final List<Genre> genre,
            @RequestParam(required = false) final List<ProductType> type,

            @RequestParam
            @Positive final int pageSize,

            @RequestParam
            @Positive final int pageNum,

            @RequestParam
            @Min(value = 0) final BigDecimal minPrice,

            @RequestParam
            @Min(value = 0) final BigDecimal maxPrice,

            @RequestParam
            @Pattern(regexp = CommonConstants.SORT_PARAM_REGEX) final String sortBy,

            @RequestHeader(name = CommonConstants.BASIC_USER_ID_HEADER, required = false) final Long userId) {
        log.info("Fetching games by genres: {} with exclusion of types: {} and price range: {} - {}. Page: {} with {} items per page sorted by {}",
                genre, type, minPrice, maxPrice, pageNum, pageSize, sortBy);
        if (maxPrice.compareTo(minPrice) < 0) {
            log.warn("Invalid price range: minPrice {} is greater than maxPrice {}", minPrice, maxPrice);
            throw new PriceRangeException();
        }
        GameListPageDto gamePage = gameService.getByGenre(genre, type, pageSize, pageNum, minPrice, maxPrice, parseSortBy(sortBy));
        return markUserSubscription(gamePage, userId);
    }

    /**
     * Retrieves the list of games to which user subscribed.
     *
     * @param pageSize the number of elements per page.
     * @param pageNum  the page number to retrieve.
     * @param sortBy   the sort criteria in the format {@code property-DIRECTION}.
     * @param userId   the identifier of the user whose games are being requested.
     * @return a {@link GameListPageDto} containing the user's games.
     * @throws SortParamException if the sort parameter is invalid.
     */
    @GetMapping("/user/games")
    @ResponseStatus(HttpStatus.OK)
    public GameListPageDto getGamesOfUser(
            @RequestParam
            @Positive final int pageSize,

            @RequestParam
            @Positive final int pageNum,

            @RequestParam
            @Pattern(regexp = CommonConstants.SORT_PARAM_REGEX) final String sortBy,

            @RequestHeader(CommonConstants.BASIC_USER_ID_HEADER) final long userId
    ) {
        log.info("Fetching games for user {}. Page: {} with {} items per page sorted by {}",
                userId, pageNum, pageSize, sortBy);
        return gameService.getUserGames(userId, pageSize, pageNum, parseSortBy(sortBy));
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
        log.debug("Retrieving image for game '{}'", gameName);
        return resourceService.getGameImage(gameName);
    }

    /**
     * Parses the sort parameter provided in the request and returns a corresponding {@link Sort} object.
     * <p>
     * The expected format is {@code property-DIRECTION} (e.g., "name-ASC" or "gamesInShop.discountPrice-DESC").
     * </p>
     *
     * @param sortBy the sort parameter string.
     * @return a {@link Sort} object representing the sort criteria.
     * @throws SortParamException if the sort parameter does not match the required format.
     */
    private Sort parseSortBy(final String sortBy) {
        Matcher matcher = SORT_PARAM_PATTERN.matcher(sortBy);
        if (!matcher.matches()) {
            log.warn("Invalid sort parameter: {}", sortBy);
            throw new SortParamException();
        }
        String[] sortParams = sortBy.split("-");
        String property = sortParams[0];
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortParams[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, property);
    }

    /**
     * Updates each game in the given {@link GameListPageDto} with the user's subscription status.
     *
     * @param gamePage the page of game DTOs.
     * @param userId   (optional) the identifier of the user; if provided, each game is checked against the user's game list.
     * @return the updated {@link GameListPageDto} with subscription statuses set.
     */
    private GameListPageDto markUserSubscription(final GameListPageDto gamePage, final Long userId) {
        if (userId != null) {
            gamePage.getGames().forEach(gameDto -> markUserSubscription(gameDto, userId));
        }
        return gamePage;
    }

    /**
     * Updates the given game DTO with the subscription status of the user.
     *
     * @param <T>     a type that extends {@link GameDto}.
     * @param gameDto the game DTO to update.
     * @param userId  (optional) the identifier of the user; if provided, the game is marked as subscribed if present in the user's game list.
     * @return the updated game DTO.
     */
    private <T extends GameDto> T markUserSubscription(final T gameDto, final Long userId) {
        if (userId != null) {
            BasicUser user = userService.getUserById(userId);
            boolean isSubscribed = user.getGameList().stream()
                    .anyMatch(game -> game.getId() == gameDto.getId());
            gameDto.setUserSubscribed(isSubscribed);
        }
        return gameDto;
    }
}
