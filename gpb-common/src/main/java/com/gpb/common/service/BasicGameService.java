package com.gpb.common.service;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;

import java.math.BigDecimal;
import java.util.List;

public interface BasicGameService {

    /**
     * Get game by id
     *
     * @param gameId games id
     * @param userId user id
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
    GameListPageDto getByName(final String name, final int pageSize, final int pageNum, String sort);

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
                               String sort);

    /**
     * Get games of user
     *
     * @param userId   user id
     * @param pageSize amount of elements on page
     * @param pageNum  page number
     * @param sort     sort parameters
     * @return list of user`s games with all amount
     */
    GameListPageDto getUserGames(long userId, int pageSize, int pageNum, String sort);

    /**
     * Set isFollowed field in game for user by game and user id
     *
     * @param gameId   games id
     * @param userId   user id
     * @param isFollow is program would follow this game for information changing
     */
    void setFollowGameOption(long gameId, long userId, boolean isFollow);
}
