package com.gpb.backend.integration.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpb.backend.GpbWebApplication;
import com.gpb.backend.bean.event.AccountLinkerEvent;
import com.gpb.backend.bean.event.EmailEvent;
import com.gpb.backend.bean.event.GameFollowEvent;
import com.gpb.backend.bean.user.WebUser;
import com.gpb.backend.bean.user.dto.UserDto;
import com.gpb.backend.configuration.AdminUserInitializer;
import com.gpb.backend.listener.EmailNotificationListener;
import com.gpb.backend.repository.WebUserRepository;
import com.gpb.backend.rest.RestTemplateHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.gpb.backend.util.Constants.ADMIN_ROLE;
import static com.gpb.backend.util.Constants.USER_ROLE;
import static org.mockito.Mockito.when;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GpbWebApplication.class)
@AutoConfigureMockMvc
@Sql(value = "classpath:/cleaning_db.sql", executionPhase = BEFORE_TEST_METHOD)
public class BaseAuthenticationIntegration {

    protected static final String GAME_SERVICE_URL = "gameServiceUrl";
    private static final String DATE_STRING_FORMAT = "dd/MM/yyyy";

    protected static final String DECODE_PASSWORD = "pass";
    private static final Long ADMIN_BASIC_USER_ID = 1L;
    protected static final List<WebUser> userList = new ArrayList<>();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected WebUserRepository webUserRepository;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @MockBean
    protected KafkaTemplate<String, Long> kafkaGameRemoveEventTemplate;

    @MockBean
    protected KafkaTemplate<String, EmailEvent> kafkaEmailEventTemplate;

    @MockBean
    protected KafkaTemplate<String, GameFollowEvent> kafkaGameFollowEventTemplate;

    @MockBean
    protected KafkaTemplate<String, AccountLinkerEvent> kafkaAccountsLinkerEventTemplate;

    @MockBean
    protected EmailNotificationListener listener;

    @MockBean
    protected RestTemplateHandler restTemplateHandler;

    @MockBean
    protected AdminUserInitializer adminUserInitializer;

    @BeforeAll
    protected static void beforeAll() throws ParseException {
        userList.clear();
        userList.add(userCreation("email1", DECODE_PASSWORD, ADMIN_ROLE));

        System.setProperty("ADMIN_EMAIL", "admin@mail.com");
        System.setProperty("ADMIN_PASSWORD", "pass");
        System.setProperty("GAMEZEY_LOGIN", "");
        System.setProperty("GAMEZEY_PASSWORD", "");
        System.setProperty("IMAGE_FOLDER", "");
        System.setProperty("FRONT_SERVICE_URL", "");
        System.setProperty("KAFKA_SERVER_URL", "");
        System.setProperty("GAME_SERVICE_URL", GAME_SERVICE_URL);
    }

    @BeforeEach
    void userCreationForAuthBeforeAllTests() {
        when(restTemplateHandler
                .executeRequest(GAME_SERVICE_URL + "/user", HttpMethod.POST, null, Long.class))
                .thenReturn(1L)
                .thenReturn(2L);
        adminCreation(0);
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
        WebUser user = WebUser.builder()
                .email(userList.get(userIndex).getEmail())
                .password(passwordEncoder.encode(CharBuffer.wrap(userList.get(userIndex).getPassword())))
                .role(ADMIN_ROLE)
                .isActivated(userList.get(userIndex).isActivated())
                .basicUserId(ADMIN_BASIC_USER_ID)
                .locale(userList.get(userIndex).getLocale())
                .build();
        WebUser savedUser = webUserRepository.save(user);
        userList.get(userIndex).setId(savedUser.getId());
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
