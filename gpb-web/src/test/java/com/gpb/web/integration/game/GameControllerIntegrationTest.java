package com.gpb.web.integration.game;

import com.gpb.web.bean.Game;
import com.gpb.web.bean.Genre;
import com.gpb.web.repository.GameRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameControllerIntegrationTest extends BaseAuthenticationIntegration {

    @Autowired
    private GameRepository gameRepository;

    private static final List<Game> games = new ArrayList<>();


    @BeforeAll
    static void beforeAllGame() {
        games.add(gameCreation("name1", "url1", Genre.STRATEGY));
        games.add(gameCreation("name2", "url2", Genre.RPG));
        games.add(gameCreation("name3", "url3", Genre.STRATEGY));
    }

    @BeforeEach
    void gameCreationBeforeAllTests() {
        gameRepository.save(games.get(0));
        gameRepository.save(games.get(1));
        gameRepository.save(games.get(2));
    }

    @Test
    void getUserByIdSuccessfullyShouldReturnUser() throws Exception {

        mockMvc.perform(get("/game/{id}", games.get(0).getId())
                        .with(user(userList.get(0).getEmail()).password(userList.get(0).getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.url").value(games.get(0).getUrl()))
                .andExpect(jsonPath("$.name").value(games.get(0).getName()));
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
                .andExpect(jsonPath("$").value(String.format("Game with id '%s' not found", notExistingGameId)));
    }

    private static Game gameCreation(String name, String url, Genre genre) {
        return Game.builder().name(name).url(url).genre(genre).build();
    }
}
