package com.gpb.web.integration.game;

import com.gpb.web.bean.Game;
import com.gpb.web.bean.GameInShop;
import com.gpb.web.bean.Genre;
import com.gpb.web.repository.GameInShopRepository;
import com.gpb.web.repository.GameRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private static final String DATE_STRING_FORMAT = "dd/MM/yyyy";

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameInShopRepository gameInShopRepository;

    private static final List<Game> games = new ArrayList<>();


    @BeforeAll
    static void beforeAllGame() throws ParseException {
        games.add(gameCreation("name1", "url1", Genre.STRATEGY));
        games.add(gameCreation("name2", "url2", Genre.RPG));
        games.add(gameCreation("name3", "url3", Genre.STRATEGY));
    }

    @BeforeEach
    void gameCreationBeforeAllTests() {
        gameRepository.save(games.get(0));
        gameRepository.save(games.get(1));
        gameRepository.save(games.get(2));
        games.forEach(game -> game.getGamesInShop()
                .forEach(gameInShop -> gameInShopRepository.save(gameInShop)));
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
                .andExpect(jsonPath("$").value(String.format("Game with id '%s' not found", notExistingGameId)));
    }

    private static GameInShop gameInShopCreation(String url, BigDecimal price, int discount) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_STRING_FORMAT);
        return GameInShop.builder()
                .url(url)
                .price(price)
                .discount(discount)
                .discountDate(dateFormat.parse("12/12/2021"))
                .isAvailable(true)
                .build();
    }

    private static Game gameCreation(String name, String url, Genre genre) throws ParseException {
        Game game = Game.builder()
                .name(name)
                .gamesInShop(List.of(gameInShopCreation(url, new BigDecimal("50.0"), 15)))
                .genre(genre).build();
        game.getGamesInShop().forEach(gameInShop -> gameInShop.setGame(game));
        return game;
    }
}
