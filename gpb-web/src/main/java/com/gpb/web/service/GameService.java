package com.gpb.web.service;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.GameInfoDto;
import com.gpb.web.bean.game.GameListPageDto;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.bean.game.ProductType;
import com.gpb.web.bean.user.WebUser;
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
     * Get game by id
     *
     * @param gameId games id
     * @param userId users id
     * @return game
     */
    GameInfoDto getById(long gameId, long userId);

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
     * Create game
     *
     * @param game game that would be created
     * @return created game
     */
    Game create(Game game);

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
    List<GameInShop> getUsersChangedGames(WebUser user, List<GameInShop> changedGames);

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
     * Begins to follow game for information changing if still not follows
     *
     * @param gameId games id
     */
    void followGame(long gameId);

    /**
     * Stops to follow game for information changing if any user not subscribed to it
     *
     * @param gameId games id
     */
    void unfollowGame(long gameId);
}
