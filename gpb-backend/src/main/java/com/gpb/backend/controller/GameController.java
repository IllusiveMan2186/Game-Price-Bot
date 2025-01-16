package com.gpb.backend.controller;

import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.GameService;
import com.gpb.backend.service.ResourceService;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.exception.PriceRangeException;
import com.gpb.common.exception.SortParamException;
import com.gpb.common.util.CommonConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    private final ResourceService resourceService;

    public GameController(GameService gameService, ResourceService resourceService) {
        this.gameService = gameService;
        this.resourceService = resourceService;
    }

    /**
     * Get game by id
     *
     * @param gameId games id
     * @param user   current user
     * @return game
     */
    @GetMapping(value = "/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public GameInfoDto getGameById(@PathVariable final long gameId, @AuthenticationPrincipal UserDto user) {
        long userId = user == null ? -1 : user.getId();
        return gameService.getById(gameId, userId);
    }

    /**
     * Get game by name
     *
     * @param name games name
     * @return game
     */
    @GetMapping(value = "/name/{name}")
    public GameListPageDto getGameByName(@PathVariable final String name,
                                         @RequestParam(required = false, defaultValue = "25") final int pageSize,
                                         @RequestParam(required = false, defaultValue = "1") final int pageNum,
                                         @RequestParam(required = false, defaultValue = "gamesInShop.discountPrice-ASC") final String sortBy) {
        checkSortParam(sortBy);
        return gameService.getByName(name, pageSize, pageNum, sortBy);
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
                                            @RequestParam(required = false, defaultValue = "25") final int pageSize,
                                            @RequestParam(required = false, defaultValue = "1") final int pageNum,
                                            @RequestParam(required = false, defaultValue = "0") final BigDecimal minPrice,
                                            @RequestParam(required = false, defaultValue = "10000") final BigDecimal maxPrice,
                                            @RequestParam(required = false, defaultValue = "gamesInShop.discountPrice-ASC") final String sortBy) {
        log.info("Get games by genres : '{}',types to exclude - '{}',price '{}' - '{}' with '{}' " +
                        "element on page for '{}' page and sort '{}' ",
                genre, type, minPrice, maxPrice, pageSize, pageNum, sortBy);
        checkSortParam(sortBy);
        if (maxPrice.compareTo(minPrice) < 0) {
            log.info("Invalid price range '{}' - '{}'", minPrice, maxPrice);
            throw new PriceRangeException();
        }
        return gameService.getByGenre(genre, type, pageSize, pageNum, minPrice, maxPrice, sortBy);
    }

    /**
     * Get user games
     *
     * @param pageSize amount of elements on page
     * @param pageNum  page number
     * @param user     current user
     * @param sortBy   sort parameter
     * @return list of games
     */
    @GetMapping(value = "/user/games")
    @ResponseStatus(HttpStatus.OK)
    public GameListPageDto getGamesOfUser(@RequestParam(required = false, defaultValue = "25") final int pageSize,
                                          @RequestParam(required = false, defaultValue = "1") final int pageNum,
                                          @RequestParam(required = false, defaultValue = "gamesInShop.discountPrice-ASC") final String sortBy,
                                          @AuthenticationPrincipal UserDto user) {
        log.info("Get games for user '{}' with '{}' element on page for '{}' page and sort '{}' ",
                user.getId(), pageSize, pageNum, sortBy);
        checkSortParam(sortBy);
        return gameService.getUserGames(user.getBasicUserId(), pageSize, pageNum, sortBy);
    }

    /**
     * Remove game by id
     *
     * @param gameId games id
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeGameById(@PathVariable final long gameId) {
        gameService.removeGame(gameId);
    }

    /**
     * Remove game in store by id
     *
     * @param gameInStoreId games id
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/store/{gameInStoreId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeGameInStoreById(@PathVariable final long gameInStoreId) {
        gameService.removeGameInStore(gameInStoreId);
    }


    /**
     * Get game images from folder
     *
     * @param gameName game name
     * @return image in byte array
     */
    @GetMapping(value = "/image/{gameName}")
    @ResponseStatus(HttpStatus.OK)
    public byte[] getGameImage(@PathVariable final String gameName) {
        return resourceService.getGameImage(gameName);
    }

    private void checkSortParam(String sortBy) {
        Pattern pattern = java.util.regex.Pattern.compile(CommonConstants.SORT_PARAM_REGEX);
        Matcher matcher = pattern.matcher(sortBy);

        if (!matcher.matches()) {
            log.info("Invalid sort params '{}'", sortBy);
            throw new SortParamException();
        }
    }
}
