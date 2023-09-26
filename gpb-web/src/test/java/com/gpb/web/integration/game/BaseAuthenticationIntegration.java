package com.gpb.web.integration.game;

import com.gpb.web.GpbWebApplication;
import com.gpb.web.bean.WebUser;
import com.gpb.web.repository.WebUserRepository;
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

import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GpbWebApplication.class)
@AutoConfigureMockMvc
@Sql(value = "classpath:/cleaning_db.sql", executionPhase = BEFORE_TEST_METHOD)
public class BaseAuthenticationIntegration {

    protected static final String DECODE_PASSWORD = "$2a$04$6B90esin.A8CPQ7PY2EheOu7nFzKBrHGlWlNyKlmtRCPPiikObH/W";
    protected static final String ENCODE_PASSWORD = "pass";

    protected static final List<WebUser> userList = new ArrayList<>();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected WebUserRepository repository;
    @Autowired
    protected UserService service;

    @BeforeAll
    protected static void beforeAll() {
        userList.clear();
        userList.add(userCreation("email1", DECODE_PASSWORD));
    }

    @BeforeEach
    void userCreationForAuthBeforeAllTests() {

        service.createUser(userList.get(0));
    }

    protected static WebUser userCreation(String email) {
        return WebUser.builder().email(email).build();
    }

    protected static WebUser userCreation(String email, String password) {
        WebUser user = userCreation(email);
        user.setPassword(password);

        return user;
    }
}
