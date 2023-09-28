package com.gpb.web.service;

import com.gpb.web.bean.Game;

public interface GameStoresService {

    Game findOrCreateGameByName(String name);

    Game findOrCreateGameByUrl(String url);
}
