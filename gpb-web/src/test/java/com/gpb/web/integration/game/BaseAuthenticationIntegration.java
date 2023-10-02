package com.gpb.web.integration.game;

import com.gpb.web.GpbWebApplication;
import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.repository.GameInShopRepository;
import com.gpb.web.repository.GameRepository;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GpbWebApplication.class)
@AutoConfigureMockMvc
@Sql(value = "classpath:/cleaning_db.sql", executionPhase = BEFORE_TEST_METHOD)
public class BaseAuthenticationIntegration {

    private static final String DATE_STRING_FORMAT = "dd/MM/yyyy";

    protected static final String DECODE_PASSWORD = "$2a$04$6B90esin.A8CPQ7PY2EheOu7nFzKBrHGlWlNyKlmtRCPPiikObH/W";

    protected static final String ENCODE_PASSWORD = "pass";

    protected static final List<WebUser> userList = new ArrayList<>();
    protected static final List<Game> games = new ArrayList<>();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserService userService;

    @Autowired
    protected GameRepository gameRepository;

    @Autowired
    protected GameInShopRepository gameInShopRepository;

    @BeforeAll
    protected static void beforeAll() throws ParseException {
        userList.clear();
        userList.add(userCreation("email1", DECODE_PASSWORD));

        games.clear();
        games.add(gameCreation("name1", "url1", Genre.STRATEGY));
        games.add(gameCreation("name2", "url2", Genre.RPG));
        games.add(gameCreation("name3", "url3", Genre.STRATEGY));
    }

    @BeforeEach
    void userCreationForAuthBeforeAllTests() {

        userService.createUser(userList.get(0));

        gameRepository.save(games.get(0));
        gameRepository.save(games.get(1));
        gameRepository.save(games.get(2));
        games.forEach(game -> game.getGamesInShop()
                .forEach(gameInShop -> gameInShopRepository.save(gameInShop)));
    }

    protected static WebUser userCreation(String email, String password) {
        return WebUser.builder().email(email).password(password).build();
    }

    protected static GameInShop gameInShopCreation(String url, BigDecimal price, int discount) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_STRING_FORMAT);
        return GameInShop.builder()
                .url(url)
                .price(price)
                .discount(discount)
                .discountDate(dateFormat.parse("12/12/2021"))
                .isAvailable(true)
                .build();
    }

    protected static Game gameCreation(String name, String url, Genre genre) throws ParseException {
        Game game = Game.builder()
                .name(name)
                .gamesInShop(List.of(gameInShopCreation(url, new BigDecimal("50.0"), 15)))
                .genre(genre).build();
        game.getGamesInShop().forEach(gameInShop -> gameInShop.setGame(game));
        return game;
    }
}
