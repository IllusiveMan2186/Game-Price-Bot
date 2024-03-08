package com.gpb.web.service;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.BasicUser;

import java.util.List;

/**
 * Class for handling game entity
 */
public interface GameService {

    /**
     * Get game by id
     *
     * @param gameId games id
     * @return game
     */
    Game getById(long gameId);

    /**
     * Get game for which subscribe users
     *
     * @return game for which subscribe users
     */
    List<GameInShop> getSubscribedGames();

    /**
     * Save game info changes
     *
     * @param changedGames game that change info
     */
    void changeInfo(List<GameInShop> changedGames);

    /**
     * Get changed games for that user subscribed on
     *
     * @param user         user
     * @param changedGames list of all changed games
     * @return changed games for that user subscribed on
     */
    List<GameInShop> getUsersChangedGames(BasicUser user, List<GameInShop> changedGames);

    /**
     * Add games to repository
     *
     * @param games games to add
     * @return list of games ids
     */
    List<Long> addGames(List<Game> games);
}
