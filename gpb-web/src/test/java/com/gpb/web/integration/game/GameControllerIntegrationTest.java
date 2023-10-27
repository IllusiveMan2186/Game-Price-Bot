package com.gpb.web.integration.game;

import com.gpb.web.bean.game.GameInShop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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

        mockMvc.perform(get("/game/{id}", games.get(0).getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(games.get(0).getName()));
    }

    @Test
    void getGameByUrlSuccessfullyShouldReturnGame() throws Exception {
        GameInShop gameInShop = games.get(0).getGamesInShop().get(0);

        mockMvc.perform(get("/game/url?url={url}", games.get(0).getGamesInShop().get(0).getUrl()))
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
    void getGamesByGenreSuccessfullyShouldReturnListOfGames() throws Exception {
        mockMvc.perform(get("/game/genre?genre={genre}&genre={genre}&pageNum=1&pageSize={size}",
                        games.get(0).getGenres().get(0), games.get(1).getGenres().get(0), games.size() + 1))
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
        mockMvc.perform(get("/game/genre"))
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
    void getGamesByGenreSuccessfullyShouldReturnSecondPageOfGame() throws Exception {
        mockMvc.perform(get("/game/genre?genre={genre}&pageNum=2&pageSize=1", games.get(0).getGenres().get(0)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.elementAmount").value(2))
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games", hasSize(1)))
                .andExpect(jsonPath("$.games[0].id").value(3));
    }

    @Test
    void getUserByNotExistingIdShouldReturnError() throws Exception {
        int notExistingGameId = games.size() + 1;

        mockMvc.perform(get("/game/{id}", notExistingGameId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("app.game.error.id.not.found"));
    }

}
