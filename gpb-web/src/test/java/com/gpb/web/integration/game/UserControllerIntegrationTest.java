package com.gpb.web.integration.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpb.web.bean.WebUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
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
        userList.add(userCreation("email2", DECODE_PASSWORD));
    }

    @BeforeEach
    void userCreationBeforeAllTests() {
        service.createUser(userList.get(1));
    }

    @Test
    void testAccessToGetUserInfoShouldNotHaveAccess() throws Exception {
        mockMvc.perform(get("/user/info/{id}", userList.get(0).getId()))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    void getUserByIdSuccessfullyShouldReturnUser() throws Exception {
        mockMvc.perform(get("/user/info/{id}", userList.get(0).getId())
                        .with(user(userList.get(0).getEmail()).password(userList.get(0).getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(userList.get(0).getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void getUserByNotExistingIdShouldReturnErrorMessage() throws Exception {
        int notExistingUserId = userList.size() + 1;

        mockMvc.perform(get("/user/info/{id}", notExistingUserId)
                        .with(user(userList.get(0).getEmail()).password(userList.get(0).getPassword())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(String.format("User with id '%s' not found", notExistingUserId)));
    }

    @Test
    void createUserWithAlreadyRegisteredEmailShouldReturnErrorMessage() throws Exception {
        mockMvc.perform(post("/user/registration")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(userList.get(0))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User with this email already exist"));
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

    @Test
    void loginSuccessfullyShouldSetUserInfoInSession() throws Exception {
        MvcResult result = mockMvc.perform(formLogin("/login").user(userList.get(0).getEmail())
                        .password(ENCODE_PASSWORD))
                .andDo(print())
                .andExpect(status().isFound())
                .andReturn();
        assertNotNull(result.getRequest().getSession());
        SecurityContextImpl securityContext = (SecurityContextImpl) result.getRequest()
                .getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        assertTrue(securityContext.getAuthentication().isAuthenticated());
        WebUser resultUser = (WebUser) securityContext.getAuthentication().getPrincipal();
        assertEquals(1, resultUser.getId());
        assertEquals(userList.get(0).getEmail(), resultUser.getEmail());

    }


    private String objectToJson(WebUser user) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(user);
    }
}
