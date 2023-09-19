package com.gpb.web.service.impl;

import com.gpb.web.bean.Game;
import com.gpb.web.bean.Genre;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.exception.UrlAlreadyExistException;
import com.gpb.web.repository.GameRepository;
import com.gpb.web.service.GameService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    //private static final Log log = LogFactory.getLog(EventServiceImpl.class.getName());

    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Game getById(final long gameId) {
        final Game game = gameRepository.findById(gameId);
        if (game == null) {
            throw new NotFoundException(String.format("Game with id '%s' not found", gameId));
        }

        //log.info(String.format("Get game by id:%s", eventId));

        return game;
    }

    @Override
    public Game getByName(final String name) {
        final Game game = gameRepository.findByName(name);
        if (game == null) {
            throw new NotFoundException(String.format("Game with name '%s' not found", name));
        }

        //log.info(String.format("Get game by id:%s", eventId));

        return game;
    }

    @Override
    public Game getByUrl(final String url) {
        final Game game = gameRepository.findByUrl(url);
        if (game == null) {
            throw new NotFoundException(String.format("Game with url '%s' not found", url));
        }

        //log.info(String.format("Get game by id:%s", eventId));

        return game;
    }

    @Override
    public List<Game> getByGenre(final Genre genre, final int pageSize, final int pageNum) {
        //log.info(String.format("Create event:%s", event));

        return gameRepository.findByGenre(genre, PageRequest.of(pageNum - 1, pageSize));
    }

    @Override
    public Game create(Game game) {
        if(gameRepository.findByUrl(game.getUrl()) != null){
            throw new UrlAlreadyExistException();
        }
        //log.info(String.format("Create event:%s", event));

        return gameRepository.save(game);
    }
}
