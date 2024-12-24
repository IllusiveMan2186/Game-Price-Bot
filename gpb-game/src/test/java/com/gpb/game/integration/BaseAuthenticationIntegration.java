package com.gpb.game.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpb.common.entity.event.EmailNotificationEvent;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.util.CommonConstants;
import com.gpb.game.GpbStoresApplication;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.listener.GameRequestListener;
import com.gpb.game.repository.GameInShopRepository;
import com.gpb.game.repository.GameRepository;
import com.gpb.game.repository.UserRepository;
import com.gpb.game.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GpbStoresApplication.class)
@AutoConfigureMockMvc
@Sql(value = "classpath:/cleaning_db.sql", executionPhase = BEFORE_TEST_METHOD)
class BaseAuthenticationIntegration {

    private static final String DATE_STRING_FORMAT = "dd/MM/yyyy";

    protected static final String API_KEY = "api key";

    protected static final List<Game> games = new ArrayList<>();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected GameRepository gameRepository;

    @Autowired
    protected GameInShopRepository gameInShopRepository;

    @MockBean
    protected KafkaTemplate<String, EmailNotificationEvent> responseKafkaTemplate;
    @MockBean
    protected GameRequestListener gameRequestListener;

    @BeforeAll
    protected static void beforeAll() throws ParseException {
        games.clear();
        games.add(gameCreation("name1", "url1", Genre.STRATEGIES, new BigDecimal("100.0"), new BigDecimal("100.0")));
        games.add(gameCreation("name2", "url2", Genre.RPG, new BigDecimal("500.0"), new BigDecimal("500.0")));
        games.add(gameCreation("name3", "url3", Genre.STRATEGIES, new BigDecimal("1000.0"), new BigDecimal("800.0")));
        System.setProperty("IMAGE_FOLDER", "");
        System.setProperty("KAFKA_SERVER_URL", "");
        System.setProperty("API_KEY", API_KEY);
    }

    @BeforeEach
    @Transactional
    void userCreationForAuthBeforeAllTests() {
        games.set(0, gameRepository.save(games.get(0)));
        games.set(1, gameRepository.save(games.get(1)));
        games.set(2, gameRepository.save(games.get(2)));
        games.forEach(game -> game.getGamesInShop()
                .forEach(gameInShop -> gameInShopRepository.save(gameInShop)));
    }

    @Test
    void testRequestFilter_whenApiKeyMissing_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/game/{id}", games.get(0).getId())
                        .header(CommonConstants.BASIC_USER_ID_HEADER, -1))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    protected static GameInShop gameInShopCreation(String url, BigDecimal price, BigDecimal discountPrice)
            throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_STRING_FORMAT);
        return GameInShop.builder()
                .url(url)
                .price(price)
                .discount(15)
                .discountPrice(discountPrice)
                .discountDate(dateFormat.parse("12/12/2021"))
                .isAvailable(true)
                .build();
    }

    protected static Game gameCreation(String name, String url, Genre genre, BigDecimal price, BigDecimal discountPrice)
            throws ParseException {
        Game game = Game.builder()
                .name(name)
                .type(ProductType.GAME)
                .gamesInShop(Set.of(gameInShopCreation(url, price, discountPrice)))
                .isFollowed(true)
                .genres(Collections.singletonList(genre)).build();
        game.getGamesInShop().forEach(gameInShop -> gameInShop.setGame(game));
        return game;
    }

    protected String objectToJson(Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
