package com.gpb.game.service;

import com.gpb.game.bean.game.Game;
import com.gpb.game.bean.game.GameInShop;
import com.gpb.game.bean.game.GameInfoDto;
import com.gpb.game.bean.game.GameListPageDto;
import com.gpb.game.bean.game.Genre;
import com.gpb.game.bean.game.ProductType;
import com.gpb.game.bean.user.BasicUser;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
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
     * Get game dto by id
     *
     * @param gameId games id
     * @return game
     */
    GameInfoDto getDtoById(long gameId);

    /**
     * Get game by name
     *
     * @param name     games name
     * @param pageSize amount of elements on page
     * @param pageNum  page number
     * @param sort     sort parameters
     * @return game
     */
    GameListPageDto getByName(final String name, final int pageSize, final int pageNum, Sort sort);

    /**
     * Get game by url
     *
     * @param url game url from the store
     * @return game
     */
    GameInfoDto getByUrl(String url);

    /**
     * Get games by genre
     *
     * @param genre          genres of the game
     * @param typesToExclude types of the product to exclude from search
     * @param pageSize       amount of elements on page
     * @param pageNum        page number
     * @param minPrice       minimal price
     * @param maxPrice       maximal price
     * @param sort           sort parameters
     * @return list of games with all amount
     */
    GameListPageDto getByGenre(List<Genre> genre, List<ProductType> typesToExclude, int pageSize, int pageNum,
                               BigDecimal minPrice, BigDecimal maxPrice,
                               Sort sort);

    /**
     * Get games of user
     *
     * @param userId   user id
     * @param pageSize amount of elements on page
     * @param pageNum  page number
     * @param sort     sort parameters
     * @return list of user`s games with all amount
     */
    GameListPageDto getUserGames(long userId, int pageSize, int pageNum, Sort sort);

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
     * Remove game by id
     *
     * @param gameId games id
     */
    void removeGame(long gameId);

    /**
     * Remove game in store by id
     *
     * @param gameInStoreId games id
     */
    void removeGameInStore(long gameInStoreId);

    /**
     * Set isFollowed field in game by id
     *
     * @param gameId   games id
     * @param isFollow is program would follow this game for information changing
     * @return changed game
     */
    Game setFollowGameOption(long gameId, boolean isFollow);
}
