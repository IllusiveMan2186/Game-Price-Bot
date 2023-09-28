package com.gpb.web.controller;

import com.gpb.web.bean.Game;
import com.gpb.web.bean.Genre;
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

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Game getGamerById(@PathVariable final long id) {
        return gameService.getById(id);
    }

    @GetMapping(value = "/name/{name}")
    public Game getGameByName(@PathVariable final String name) {
        return gameService.getByName(name);
    }

    @GetMapping(value = "/url/{url}")
    public Game getGameByUrl(@PathVariable final String url) {
        return gameService.getByUrl(url);
    }

    @GetMapping(value = "/genre/{genre}")
    @ResponseStatus(HttpStatus.OK)
    public List<Game> getGamesForGenre(@PathVariable final Genre genre, @RequestParam final int pageSize,
                                       @RequestParam final int pageNum) {
        return gameService.getByGenre(genre, pageSize, pageNum);
    }
}
