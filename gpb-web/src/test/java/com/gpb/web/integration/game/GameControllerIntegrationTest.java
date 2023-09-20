package com.gpb.web.integration.game;

import com.gpb.web.GpbWebApplication;
import com.gpb.web.bean.Game;
import com.gpb.web.bean.Genre;
import com.gpb.web.controller.GameController;
import com.gpb.web.repository.GameRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GpbWebApplication.class)
@AutoConfigureMockMvc
@Sql(value = "classpath:/cleaning_db.sql", executionPhase = BEFORE_TEST_METHOD)
public class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameController controller;

    @Autowired
    private GameRepository repository;

    private static final List<Game> games = new ArrayList<>();


    @BeforeAll
    static void beforeAll() {
        games.add(gameCreation("name1", "url1", Genre.STRATEGY));
        games.add(gameCreation("name2", "url2", Genre.RPG));
        games.add(gameCreation("name3", "url3", Genre.STRATEGY));
    }

    @BeforeEach
    void userCreationBeforeAllTests() {
        repository.save(games.get(0));
        repository.save(games.get(1));
        repository.save(games.get(2));
    }

    @Test
    void getUserByIdSuccessfullyShouldReturnUser() throws Exception {

        mockMvc.perform(get("/game/{id}", games.get(0).getId()))
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
                        games.size() + 1))
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
        mockMvc.perform(get("/game/genre/{genre}?pageNum=2&pageSize=1", games.get(0).getGenre()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id").value(3));
    }

    private static Game gameCreation(String name, String url, Genre genre) {
        return Game.builder().name(name).url(url).genre(genre).build();
    }
}
