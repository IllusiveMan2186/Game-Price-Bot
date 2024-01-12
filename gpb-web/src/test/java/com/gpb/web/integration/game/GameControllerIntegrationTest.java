package com.gpb.web.integration.game;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.bean.game.ProductType;
import com.gpb.web.bean.user.UserRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameControllerIntegrationTest extends BaseAuthenticationIntegration {

    @BeforeEach
    void gameCreationBeforeAllTests() {
    }

    @Test
    @WithMockUser(username = "email1")
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
        mockMvc.perform(get("/game/url?url={url}", games.get(0).getGamesInShop().stream().toList().get(0).getUrl()))
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
    void getSecondPageOfGamesByGenreSuccessfullyShouldReturnSecondPageOfGame() throws Exception {
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

        mockMvc.perform(get("/game/genre?minPrice={minPrice}&maxPrice={maxPrice}", "500", "1000"))
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

        mockMvc.perform(get("/game/genre?minPrice={minPrice}&maxPrice={maxPrice}", "500", "500"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.elementAmount").value(1))
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games", hasSize(1)))
                .andExpect(jsonPath("$.games[0].id").value(2));
    }

    @Test
    void getUserGamesSuccessfullyShouldReturnGame() throws Exception {
        SecurityContextImpl securityContext = getSecurityContext();
        mockMvc.perform(post("/user/games/{gameId}", games.get(1).getId())
                .contentType(APPLICATION_JSON)
                .sessionAttr("SPRING_SECURITY_CONTEXT", securityContext));

        mockMvc.perform(get("/game/user/games")
                        .sessionAttr("SPRING_SECURITY_CONTEXT", securityContext))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.elementAmount").value(1))
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games", hasSize(1)))
                .andExpect(jsonPath("$.games[0].id").value(2));
    }

    @Test
    @WithMockUser(username = "email1")
    void getUserByNotExistingIdShouldReturnError() throws Exception {
        int notExistingGameId = games.size() + 1;

        mockMvc.perform(get("/game/{id}", notExistingGameId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("app.game.error.id.not.found"));
    }

    @WithMockUser(username = "email1", roles = { "ADMIN" })
    @Test
    void removeGameWithAdminUserSuccessfullyShouldRemoveGame() throws Exception {
        mockMvc.perform(delete("/game/1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "email2")
    @Test
    void removeGameWithNotAdminUserUnsuccessfullyShouldForbidAccess() throws Exception {
        userList.add(userCreation("email2", DECODE_PASSWORD));
        userService.createUser(new UserRegistration(userList.get(1)));

        mockMvc.perform(delete("/game/1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "email1", roles = { "ADMIN" })
    @Test
    void removeGameInStoreWithAdminUserSuccessfullyShouldRemoveGame() throws Exception {
        mockMvc.perform(delete("/game/store/1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "email2")
    @Test
    void removeGameInStoreWithNotAdminUserUnsuccessfullyShouldForbidAccess() throws Exception {
        userList.add(userCreation("email2", DECODE_PASSWORD));
        userService.createUser(new UserRegistration(userList.get(1)));

        mockMvc.perform(delete("/game/store/1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
