package com.gpb.web.integration.game;

import com.gpb.web.bean.game.GameInShop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameControllerIntegrationTest extends BaseAuthenticationIntegration {

    @BeforeEach
    void gameCreationBeforeAllTests() {
    }

    @Test
    void getGameByIdSuccessfullyShouldReturnGame() throws Exception {

        mockMvc.perform(get("/game/{id}", games.get(0).getId())
                        .with(user(userList.get(0).getEmail()).password(userList.get(0).getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(games.get(0).getName()));
    }

    @Test
    void getGameByUrlSuccessfullyShouldReturnGame() throws Exception {
        GameInShop gameInShop = games.get(0).getGamesInShop().get(0);

        mockMvc.perform(get("/game/url/{url}", games.get(0).getGamesInShop().get(0).getUrl())
                        .with(user(userList.get(0).getEmail()).password(userList.get(0).getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(games.get(0).getName()))
                .andExpect(jsonPath("$.gamesInShop").isArray())
                .andExpect(jsonPath("$.gamesInShop", hasSize(games.get(0).getGamesInShop().size())))
                .andExpect(jsonPath("$.gamesInShop[0].id").value(1))
                .andExpect(jsonPath("$.gamesInShop[0].url").value(gameInShop.getUrl()))
                .andExpect(jsonPath("$.gamesInShop[0].price").value(gameInShop.getPrice()))
                .andExpect(jsonPath("$.gamesInShop[0].available").value(gameInShop.isAvailable()))
                .andExpect(jsonPath("$.gamesInShop[0].discount").value(gameInShop.getDiscount()))
                .andExpect(jsonPath("$.gamesInShop[0].discountDate").value("2021-12-11T22:00:00.000+00:00"))
        ;
    }

    @Test
    void getGamesByGenreSuccessfullyShouldReturnListOfGames() throws Exception {
        mockMvc.perform(get("/game/genre/{genre}?pageNum=1&pageSize={size}", games.get(0).getGenre(),
                        games.size() + 1)
                        .with(user(userList.get(0).getEmail()).password(userList.get(0).getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[1].id").value(3));
    }

    @Test
    void getGamesByGenreSuccessfullyShouldReturnSecondPageOfGame() throws Exception {
        mockMvc.perform(get("/game/genre/{genre}?pageNum=2&pageSize=1", games.get(0).getGenre())
                        .with(user(userList.get(0).getEmail()).password(userList.get(0).getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id").value(3));
    }

    @Test
    void getUserByNotExistingIdShouldReturnError() throws Exception {
        int notExistingGameId = games.size() + 1;

        mockMvc.perform(get("/game/{id}", notExistingGameId)
                        .with(user(userList.get(0).getEmail()).password(userList.get(0).getPassword())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("app.game.error.id.not.found"));
    }

}
