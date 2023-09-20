package com.gpb.web.integration.game;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpb.web.GpbWebApplication;
import com.gpb.web.bean.WebUser;
import com.gpb.web.controller.UserController;
import com.gpb.web.repository.WebUserRepository;
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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GpbWebApplication.class)
@AutoConfigureMockMvc
@Sql(value = "classpath:/cleaning_db.sql", executionPhase = BEFORE_TEST_METHOD)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController controller;

    @Autowired
    private WebUserRepository repository;

    private static final List<WebUser> userList = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        userList.add(userCreation("name1", "email1"));
        userList.add(userCreation("name2", "email2"));
    }

    @BeforeEach
    void userCreationBeforeAllTests() {

        repository.save(userList.get(0));
        repository.save(userList.get(1));
    }

    @Test
    void getUserByIdSuccessfullyShouldReturnUser() throws Exception {
        mockMvc.perform(get("/user/{id}", userList.get(0).getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value(userList.get(0).getUsername()))
                .andExpect(jsonPath("$.email").value(userList.get(0).getEmail()));
    }

    @Test
    void createUserSuccessfullyShouldReturnUser() throws Exception {
        WebUser user = userCreation("name3", "email3","password");

        mockMvc.perform(post("/user")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(user)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void removeUserByIdSuccessfullyShouldReturnTrue() throws Exception {
        mockMvc.perform(delete("/user/{id}", userList.get(0).getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    private static WebUser userCreation(String username, String email) {
        return WebUser.builder().username(username).email(email).build();
    }

    private static WebUser userCreation(String username, String email,String password) {
        WebUser user = userCreation(username,email);
        user.setPassword(password);

        return user;
    }

    private String objectToJson(WebUser user) throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(user);
        json = json.replaceAll("}",String.format(",\"password\":\"%s\"}", user.getPassword()));
        return json;
    }
}
