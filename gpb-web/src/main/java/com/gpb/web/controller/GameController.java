package com.gpb.web.controller;

import com.gpb.web.bean.game.GameInfoDto;
import com.gpb.web.bean.game.GameListPageDto;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.service.GameService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Get game by id
     *
     * @param id games id
     * @return game
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GameInfoDto getGamerById(@PathVariable final long id) {
        return gameService.getById(id);
    }

    /**
     * Get game by name
     *
     * @param name games name
     * @return game
     */
    @GetMapping(value = "/name/{name}")
    public GameInfoDto getGameByName(@PathVariable final String name) {
        return gameService.getByName(name);
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
     * @param genre    genre of the game
     * @param pageSize amount of elements on page
     * @param pageNum  page number
     * @return list of games
     */
    @GetMapping(value = "/genre")
    @ResponseStatus(HttpStatus.OK)
    public GameListPageDto getGamesForGenre(@RequestParam(required = false) final List<Genre> genre,
                                            @RequestParam(required = false, defaultValue = "25") final int pageSize,
                                            @RequestParam(required = false, defaultValue = "1") final int pageNum) {
        log.info(String.format("Get games by genres : '%s' with '%s' element on page for '%s' page ",
                genre, pageSize, pageNum));
        return gameService.getByGenre(genre, pageSize, pageNum);
    }
}
