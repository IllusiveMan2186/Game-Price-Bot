package com.gpb.web.integration.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpb.web.GpbWebApplication;
import com.gpb.web.bean.EmailEvent;
import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.bean.game.ProductType;
import com.gpb.web.bean.user.BasicUser;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.repository.GameInShopRepository;
import com.gpb.web.repository.GameRepository;
import com.gpb.web.repository.UserRepository;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.gpb.web.util.Constants.ADMIN_ROLE;
import static com.gpb.web.util.Constants.USER_ROLE;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GpbWebApplication.class)
@AutoConfigureMockMvc
@Sql(value = "classpath:/cleaning_db.sql", executionPhase = BEFORE_TEST_METHOD)
public class BaseAuthenticationIntegration {

    private static final String DATE_STRING_FORMAT = "dd/MM/yyyy";

    protected static final String DECODE_PASSWORD = "pass";

    protected static final List<WebUser> userList = new ArrayList<>();
    protected static final List<Game> games = new ArrayList<>();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected WebUserRepository webUserRepository;

    @Autowired
    protected GameRepository gameRepository;

    @Autowired
    protected GameInShopRepository gameInShopRepository;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @MockBean
    protected KafkaTemplate<String, String> responseKafkaTemplate;

    @MockBean
    protected ReplyingKafkaTemplate<String, String, List<String>> replyingKafkaTemplate;

    @MockBean
    protected KafkaTemplate<Long, EmailEvent> kafkaEmailEventTemplate;

    @MockBean
    protected KafkaTemplate<String, Long> kafkaFollowTemplate;

    @MockBean
    protected KafkaMessageListenerContainer<String, List<String>> replyListenerContainer;

    @BeforeAll
    protected static void beforeAll() throws ParseException {
        userList.clear();
        userList.add(userCreation("email1", DECODE_PASSWORD, ADMIN_ROLE));

        games.clear();
        games.add(gameCreation("name1", "url1", Genre.STRATEGIES, new BigDecimal("100.0"), new BigDecimal("100.0")));
        games.add(gameCreation("name2", "url2", Genre.RPG, new BigDecimal("500.0"), new BigDecimal("500.0")));
        games.add(gameCreation("name3", "url3", Genre.STRATEGIES, new BigDecimal("1000.0"), new BigDecimal("800.0")));
        System.setProperty("ADMIN_EMAIL", "");
        System.setProperty("ADMIN_PASSWORD_HASH", "");
        System.setProperty("GAMEZEY_LOGIN", "");
        System.setProperty("GAMEZEY_PASSWORD", "");
        System.setProperty("IMAGE_FOLDER", "");
        System.setProperty("WEB_SERVICE_URL", "");
        System.setProperty("KAFKA_SERVER_URL", "");
    }

    @BeforeEach
    void userCreationForAuthBeforeAllTests() {

        adminCreation(0);
        games.set(0, gameRepository.save(games.get(0)));
        games.set(1, gameRepository.save(games.get(1)));
        games.set(2, gameRepository.save(games.get(2)));
        games.forEach(game -> game.getGamesInShop()
                .forEach(gameInShop -> gameInShopRepository.save(gameInShop)));
    }

    protected static WebUser userCreation(String email, String password) {
        return WebUser.builder()
                .email(email)
                .password(password)
                .role(USER_ROLE)
                .isActivated(true)
                .locale(new Locale("ua"))
                .build();
    }

    protected static WebUser userCreation(String email, String password, String role) {
        return WebUser.builder()
                .email(email)
                .password(password)
                .role(role)
                .isActivated(true)
                .locale(new Locale("ua"))
                .build();
    }

    protected void adminCreation(int userIndex) {
        BasicUser basicUser = new BasicUser();
        userRepository.save(basicUser);
        WebUser user = WebUser.builder()
                .email(userList.get(userIndex).getEmail())
                .password(passwordEncoder.encode(CharBuffer.wrap(userList.get(userIndex).getPassword())))
                .role(ADMIN_ROLE)
                .isActivated(userList.get(userIndex).isActivated())
                .basicUser(basicUser)
                .locale(userList.get(userIndex).getLocale())
                .build();
        WebUser savedUser =webUserRepository.save(user);
        userList.get(userIndex).setId(savedUser.getId());
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

    protected SecurityContextImpl getSecurityContext() {
        return getSecurityContext(0);
    }

    protected SecurityContextImpl getSecurityContext(int userIndex) {
        WebUser user = userList.get(userIndex);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setId(user.getId());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userDto, user.getPassword(), userDto.getAuthorities());
        return new SecurityContextImpl(token);
    }
}
