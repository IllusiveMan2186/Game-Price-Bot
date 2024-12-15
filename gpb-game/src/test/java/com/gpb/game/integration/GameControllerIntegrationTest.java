package com.gpb.game.integration;

import com.gpb.game.bean.game.Game;
import com.gpb.game.bean.game.GameInShop;
import com.gpb.game.bean.game.Genre;
import com.gpb.game.bean.game.ProductType;
import com.gpb.game.bean.user.BasicUser;
import com.gpb.game.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GameControllerIntegrationTest extends BaseAuthenticationIntegration {

    @BeforeEach
    void gameCreationBeforeAllTests() {
    }

    @Test
    void getGameByIdSuccessfullyShouldReturnGame() throws Exception {

        mockMvc.perform(get("/game/{id}", games.get(0).getId())
                        .header(Constants.API_KEY_HEADER, API_KEY))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(games.get(0).getName()));
    }

    @Test
    void getGameByUrlSuccessfullyShouldReturnGame() throws Exception {
        mockMvc.perform(get("/game/url")
                        .param("url", games.get(0).getGamesInShop().stream().toList().get(0).getUrl())
                        .header(Constants.API_KEY_HEADER, API_KEY))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(games.get(0).getName()))
                .andExpect(jsonPath("$.genres").isArray())
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres[0]").value(games.get(0).getGenres().get(0).name()))
        ;
    }

    @Test
    void getGamesByGenreWithoutParametersSuccessfullyShouldReturnListOfGames() throws Exception {
        mockMvc.perform(get("/game/genre")
                        .param("genre", games.get(0).getGenres().get(0).name())
                        .param("genre", games.get(1).getGenres().get(0).name())
                        .param("pageNum", "1")
                        .param("pageSize", String.valueOf(games.size() + 1))
                        .param("minPrice", "0")
                        .param("maxPrice", "10000")
                        .param("sortBy", "gamesInShop.price-ASC")
                        .header(Constants.API_KEY_HEADER, API_KEY))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.elementAmount").value(3))
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games", hasSize(3)))
                .andExpect(jsonPath("$.games[0].id").value(1))
                .andExpect(jsonPath("$.games[1].id").value(2))
                .andExpect(jsonPath("$.games[2].id").value(3));
    }

    @Test
    void getAllGamesByGenreSuccessfullyShouldReturnListOfGames() throws Exception {
        mockMvc.perform(get("/game/genre")
                        .param("pageNum", "1")
                        .param("pageSize", "25")
                        .param("minPrice", "0")
                        .param("maxPrice", "10000")
                        .param("sortBy", "gamesInShop.price-ASC")
                        .header(Constants.API_KEY_HEADER, API_KEY))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.elementAmount").value(3))
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games", hasSize(3)))
                .andExpect(jsonPath("$.games[0].id").value(1))
                .andExpect(jsonPath("$.games[1].id").value(2))
                .andExpect(jsonPath("$.games[2].id").value(3));
    }

    @Test
    void getSecondPageOfGamesByGenreSuccessfullyShouldReturnSecondPageOfGame() throws Exception {

        mockMvc.perform(get("/game/genre")
                        .param("genre", games.get(0).getGenres().get(0).name())
                        .param("pageNum", "2")
                        .param("pageSize", "1")
                        .param("minPrice", "0")
                        .param("maxPrice", "10000")
                        .param("sortBy", "gamesInShop.price-ASC")
                        .header(Constants.API_KEY_HEADER, API_KEY))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.elementAmount").value(2))
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games", hasSize(1)))
                .andExpect(jsonPath("$.games[0].id").value(3));
    }

    @Test
    void getGamesByGenreInPriceRangeSuccessfullyShouldReturnThreeGames() throws Exception {
        GameInShop gameInShop1 = gameInShopCreation("url1", new BigDecimal(100), new BigDecimal(100));
        GameInShop gameInShop2 = gameInShopCreation("url2", new BigDecimal(600), new BigDecimal(600));
        GameInShop gameInShop3 = gameInShopCreation("url3", new BigDecimal(1500), new BigDecimal(1200));

        Game game = Game.builder()
                .name("testGame")
                .gamesInShop(Set.of(gameInShop1, gameInShop2, gameInShop3))
                .genres(Collections.singletonList(Genre.STRATEGIES)).build();
        game.getGamesInShop().forEach(gameInShop -> gameInShop.setGame(game));
        game.setType(ProductType.ADDITION);
        gameRepository.save(game);
        game.getGamesInShop().forEach(gameInShop -> gameInShopRepository.save(gameInShop));

        mockMvc.perform(get("/game/genre")
                        .param("pageNum", "1")
                        .param("pageSize", "25")
                        .param("minPrice", "500")
                        .param("maxPrice", "1000")
                        .param("sortBy", "gamesInShop.price-ASC")
                        .header(Constants.API_KEY_HEADER, API_KEY))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.elementAmount").value(3))
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games", hasSize(3)))
                .andExpect(jsonPath("$.games[0].id").value(2))
                .andExpect(jsonPath("$.games[1].id").value(4))
                .andExpect(jsonPath("$.games[2].id").value(3));
    }

    @Test
    void getGamesByGenreForOnePriceSuccessfullyShouldReturnOneGame() throws Exception {

        mockMvc.perform(get("/game/genre")
                        .param("pageNum", "1")
                        .param("pageSize", "25")
                        .param("minPrice", "500")
                        .param("maxPrice", "500")
                        .param("sortBy", "gamesInShop.price-ASC")
                        .header(Constants.API_KEY_HEADER, API_KEY))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.elementAmount").value(1))
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games", hasSize(1)))
                .andExpect(jsonPath("$.games[0].id").value(2));
    }

    @Test
    void testGetGamesByGenre_whenWrongPriceRange_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/game/genre")
                        .param("pageNum", "1")
                        .param("pageSize", "25")
                        .param("minPrice", "501")
                        .param("maxPrice", "500")
                        .param("sortBy", "gamesInShop.price-ASC")
                        .header(Constants.API_KEY_HEADER, API_KEY))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserGamesSuccessfullyShouldReturnGame() throws Exception {
        BasicUser user = userRepository.save(new BasicUser());
        userService.subscribeToGame(user.getId(), games.get(1).getId());


        mockMvc.perform(get("/game/user/games")
                        .param("pageNum", "1")
                        .param("pageSize", "25")
                        .param("sortBy", "gamesInShop.price-ASC") // Correct format
                        .header(Constants.API_KEY_HEADER, API_KEY)
                        .header(Constants.BASIC_USER_ID_HEADER, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.elementAmount").value(1))
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games", hasSize(1)))
                .andExpect(jsonPath("$.games[0].id").value(games.get(1).getId()));
    }

    @Test
    void getGameByNotExistingIdShouldReturnError() throws Exception {
        int notExistingGameId = games.size() + 1;

        mockMvc.perform(get("/game/{id}", notExistingGameId)
                        .header(Constants.API_KEY_HEADER, API_KEY)
                        .header(Constants.BASIC_USER_ID_HEADER, 1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("app.game.error.id.not.found"));
    }
}
