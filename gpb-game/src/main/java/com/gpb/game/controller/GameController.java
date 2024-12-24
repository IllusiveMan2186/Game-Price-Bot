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
import java.util.regex.Pattern;


/**
 * Controller for game requests. Use only for synchronous requests where need response
 */
@Log4j2
@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final UserService userService;

    public GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    /**
     * Get game by id
     *
     * @param gameId games id
     * @param userId user who made request
     * @return game
     */
    @GetMapping(value = "/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public GameInfoDto getGameById(@PathVariable final long gameId,
                                   @RequestHeader(name = CommonConstants.BASIC_USER_ID_HEADER, required = false) Long userId) {
        log.info("Get game by id {} for user {}", gameId, userId);
        return setIsSubscribedForGame(gameService.getDtoById(gameId), userId);
    }

    /**
     * Get game by name
     *
     * @param name games name
     * @return game
     */
    @GetMapping(value = "/name/{name}")
    public GameListPageDto getGameByName(@PathVariable final String name,
                                         @RequestParam final int pageSize,
                                         @RequestParam final int pageNum,
                                         @RequestParam final String sortBy,
                                         @RequestHeader(name = CommonConstants.BASIC_USER_ID_HEADER, required = false) Long userId) {
        return setIsSubscribedForAllGames(gameService.getByName(name, pageSize, pageNum, getSortBy(sortBy)), userId);
    }

    /**
     * Get game by url
     *
     * @param url game url from the store
     * @return game
     */
    @GetMapping(value = "/url")
    public GameInfoDto getGameByUrl(@RequestParam final String url) {
        return gameService.getByUrl(url);
    }

    /**
     * Get games by genre
     *
     * @param genre    genres of the game
     * @param type     types of product to exclude from search
     * @param pageSize amount of elements on page
     * @param pageNum  page number
     * @param minPrice minimal price
     * @param maxPrice maximal price
     * @param sortBy   sort parameter
     * @return list of games
     */
    @GetMapping(value = "/genre")
    @ResponseStatus(HttpStatus.OK)
    public GameListPageDto getGamesForGenre(@RequestParam(required = false) final List<Genre> genre,
                                            @RequestParam(required = false) final List<ProductType> type,
                                            @RequestParam final int pageSize,
                                            @RequestParam final int pageNum,
                                            @RequestParam final BigDecimal minPrice,
                                            @RequestParam final BigDecimal maxPrice,
                                            @RequestParam final String sortBy,
                                            @RequestHeader(name = CommonConstants.BASIC_USER_ID_HEADER, required = false) Long userId) {
        log.info("Get games by genres : '{}',types to exclude - '{}',price '{}' - '{}' with '{}' " +
                        "element on page for '{}' page and sort '{}' ",
                genre, type, minPrice, maxPrice, pageSize, pageNum, sortBy);
        if (maxPrice.compareTo(minPrice) < 0) {
            log.info("Invalid price range '{}' - '{}'", minPrice, maxPrice);
            throw new PriceRangeException();
        }
        return setIsSubscribedForAllGames(gameService.getByGenre(genre, type, pageSize, pageNum, minPrice, maxPrice, getSortBy(sortBy)), userId);
    }

    /**
     * Get user games
     *
     * @param pageSize amount of elements on page
     * @param pageNum  page number
     * @param sortBy   sort parameter
     * @param userId   user whose games requested
     * @return list of games
     */
    @GetMapping(value = "/user/games")
    @ResponseStatus(HttpStatus.OK)
    public GameListPageDto getGamesOfUser(@RequestParam final int pageSize,
                                          @RequestParam final int pageNum,
                                          @RequestParam final String sortBy,
                                          @RequestHeader(CommonConstants.BASIC_USER_ID_HEADER) long userId) {
        log.info("Get games for user '{}' with '{}' element on page for '{}' page and sort '{}' ",
                userId, pageSize, pageNum, sortBy);
        return gameService.getUserGames(userId, pageSize, pageNum, getSortBy(sortBy));
    }

    private Sort getSortBy(String sortBy) {
        Pattern pattern = java.util.regex.Pattern.compile("(gamesInShop.(price)|(name))-(ASC|DESC)");
        Matcher matcher = pattern.matcher(sortBy);

        if (!matcher.matches()) {
            log.info("Invalid sort params '{}'", sortBy);
            throw new SortParamException();
        }
        String[] sortParams = sortBy.split("-");
        Sort.Direction direction = sortParams[1].equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, sortParams[0]);
    }

    private GameListPageDto setIsSubscribedForAllGames(GameListPageDto gameListPageDto, Long userId) {
        if (userId != null) {
            for (GameDto gameDto : gameListPageDto.getGames()) {
                setIsSubscribedForGame(gameDto, userId);
            }
        }
        return gameListPageDto;
    }

    private <T extends GameDto> T setIsSubscribedForGame(T gameDto, Long userId) {
        if (userId != null) {
            BasicUser user = userService.getUserById(userId);
            if (user.getGameList().stream().anyMatch(game -> game.getId() == gameDto.getId())) {
                gameDto.setUserSubscribed(true);
            }
        }
        return gameDto;
    }
}
