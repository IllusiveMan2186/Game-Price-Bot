package com.gpb.web.service;

import com.gpb.web.bean.Game;
import com.gpb.web.bean.Genre;

import java.util.List;

public interface GameService {

    Game getById(long gameId);

    Game getByName(String name);

    Game getByUrl(String url);

    List<Game> getByGenre(Genre genre, int pageSize, int pageNum);

    Game create(Game game);
}
