package com.gpb.web.controller;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public Game getGamerById(@PathVariable final long id) {
        return gameService.getById(id);
    }

    /**
     * Get game by name
     *
     * @param name games name
     * @return game
     */
    @GetMapping(value = "/name/{name}")
    public Game getGameByName(@PathVariable final String name) {
        return gameService.getByName(name);
    }

    /**
     * Get game by url
     *
     * @param url game url from the store
     * @return game
     */
    @GetMapping(value = "/url")
    public Game getGameByUrl(@RequestParam final String url) {
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
    @GetMapping(value = "/genre/{genre}")
    @ResponseStatus(HttpStatus.OK)
    public List<Game> getGamesForGenre(@PathVariable final Genre genre, @RequestParam final int pageSize,
                                       @RequestParam final int pageNum) {
        return gameService.getByGenre(genre, pageSize, pageNum);
    }
}
