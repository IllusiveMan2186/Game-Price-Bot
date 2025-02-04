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
import com.gpb.game.service.GameService;
import com.gpb.game.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern SORT_PARAM_PATTERN = Pattern.compile(CommonConstants.SORT_PARAM_REGEX);

    private final GameService gameService;
    private final UserService userService;

    public GameController(final GameService gameService, final UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
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
    public GameInfoDto getGameById(@PathVariable final long gameId,
                                   @RequestHeader(name = CommonConstants.BASIC_USER_ID_HEADER, required = false) final Long userId) {
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
    public GameListPageDto getGameByName(@PathVariable final String name,
                                         @RequestParam final int pageSize,
                                         @RequestParam final int pageNum,
                                         @RequestParam final String sortBy,
                                         @RequestHeader(name = CommonConstants.BASIC_USER_ID_HEADER, required = false) final Long userId) {
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
    public GameInfoDto getGameByUrl(@RequestParam final String url) {
        log.info("Fetching game by URL: {}", url);
        return gameService.getByUrl(url);
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
    public GameListPageDto getGamesForGenre(@RequestParam(required = false) final List<Genre> genres,
                                            @RequestParam(required = false) final List<ProductType> typesToExclude,
                                            @RequestParam final int pageSize,
                                            @RequestParam final int pageNum,
                                            @RequestParam final BigDecimal minPrice,
                                            @RequestParam final BigDecimal maxPrice,
                                            @RequestParam final String sortBy,
                                            @RequestHeader(name = CommonConstants.BASIC_USER_ID_HEADER, required = false) final Long userId) {
        log.info("Fetching games by genres: {} with exclusion of types: {} and price range: {} - {}. Page: {} with {} items per page sorted by {}",
                genres, typesToExclude, minPrice, maxPrice, pageNum, pageSize, sortBy);
        if (maxPrice.compareTo(minPrice) < 0) {
            log.error("Invalid price range: minPrice {} is greater than maxPrice {}", minPrice, maxPrice);
            throw new PriceRangeException();
        }
        GameListPageDto gamePage = gameService.getByGenre(genres, typesToExclude, pageSize, pageNum, minPrice, maxPrice, parseSortBy(sortBy));
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
    public GameListPageDto getGamesOfUser(@RequestParam final int pageSize,
                                          @RequestParam final int pageNum,
                                          @RequestParam final String sortBy,
                                          @RequestHeader(CommonConstants.BASIC_USER_ID_HEADER) final long userId) {
        log.info("Fetching games for user {}. Page: {} with {} items per page sorted by {}",
                userId, pageNum, pageSize, sortBy);
        return gameService.getUserGames(userId, pageSize, pageNum, parseSortBy(sortBy));
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
            log.error("Invalid sort parameter: {}", sortBy);
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
