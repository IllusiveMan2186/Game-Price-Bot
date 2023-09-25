package com.gpb.web.integration.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpb.web.bean.WebUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerIntegrationTest extends BaseAuthenticationIntegration {

    @BeforeAll
    static void beforeAllTest() {
        beforeAll();
        userList.add(userCreation("email2", "pass"));
    }

    @BeforeEach
    void userCreationBeforeAllTests() {
        userCreationForAuthBeforeAllTests();
        repository.save(userList.get(1));
    }

    @Test
    void getUserByIdSuccessfullyShouldReturnUser() throws Exception {
        mockMvc.perform(get("/user/info/{id}", userList.get(0).getId())
                        .with(user("email1").password("pass")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(userList.get(0).getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void createUserSuccessfullyShouldReturnUser() throws Exception {
        WebUser user = userCreation("email3", "password");

        mockMvc.perform(post("/user/registration")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(user)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    private String objectToJson(WebUser user) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(user);
    }
}
