package com.gpb.web.integration.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpb.web.bean.user.WebUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        userService.createUser(userList.get(1));
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
    void updateUserSuccessfullyShouldReturnUser() throws Exception {
        WebUser user = userCreation("email3", "password");
        user.setId(1);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userList.get(0), userList.get(0).getPassword(), new ArrayList<>());
        SecurityContextImpl securityContext = new SecurityContextImpl(token);

        mockMvc.perform(put("/user/info")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(user))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", securityContext))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void updateUserThatDidNotChangedInfoShouldReturnErrorMessage() throws Exception {
        WebUser user = userCreation("email1", DECODE_PASSWORD);
        user.setId(1);
        SecurityContextImpl securityContext = new SecurityContextImpl();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userList.get(0), userList.get(0).getPassword(), new ArrayList<>());

        securityContext.setAuthentication(token);

        mockMvc.perform(put("/user/info")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(user))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", securityContext))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User didn't changed during update operation"));
    }

    @Test
    void updateUserWithEmailThatAlreadyRegisteredShouldReturnUser() throws Exception {
        WebUser user = userCreation("email2", DECODE_PASSWORD);
        user.setId(1);
        SecurityContextImpl securityContext = new SecurityContextImpl();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userList.get(0), userList.get(0).getPassword(), new ArrayList<>());

        securityContext.setAuthentication(token);

        mockMvc.perform(put("/user/info")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(user))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", securityContext))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User with this email already exist"));
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

    @Test
    void addGameToUserListOfGamesShouldAddToRepository() throws Exception {
        WebUser user = userCreation("email2", DECODE_PASSWORD);
        user.setId(1);
        SecurityContextImpl securityContext = new SecurityContextImpl();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userList.get(0), userList.get(0).getPassword(), new ArrayList<>());

        securityContext.setAuthentication(token);

        mockMvc.perform(post("/user/info/games")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(1))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", securityContext))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(userList.get(0).getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.gameList").isArray())
                .andExpect(jsonPath("$.gameList", hasSize(1)))
                .andExpect(jsonPath("$.gameList[0].id").value(1))
                .andExpect(jsonPath("$.gameList[0].name").value(games.get(0).getName()))
                .andExpect(jsonPath("$.gameList[0].genre").value(games.get(0).getGenre().name()));
    }


    private String objectToJson(Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
