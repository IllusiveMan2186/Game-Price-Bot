package com.gpb.web.controller;

import com.gpb.web.bean.Game;
import com.gpb.web.bean.Genre;
import com.gpb.web.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping(value = "/id")
    public Game getGamerById(@RequestParam final long gameId) {
        return gameService.getById(gameId);
    }

    @GetMapping(value = "/name")
    public Game getGameByName(@RequestParam final String name) {
        //TODO find game or creation
        return gameService.getByName(name);
    }

    @GetMapping(value = "/url")
    public Game getGameByUrl(@RequestParam final String url) {
        //TODO creation
        return gameService.getByUrl(url);
    }

    @GetMapping(value = "/genre")
    public List<Game> getGamesForGenre(@RequestParam final Genre genre, @RequestParam final int pageSize,
                                      @RequestParam final int pageNum) {
        return gameService.getByGenre(genre,pageSize,pageNum);
    }
}
