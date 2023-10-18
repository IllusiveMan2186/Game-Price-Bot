package com.gpb.web.integration.game;

import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.WebUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.ArrayList;

import static org.springframework.http.MediaType.APPLICATION_JSON;
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
        userList.add(userCreation("email2", DECODE_PASSWORD));
        userService.createUser(new UserRegistration(userList.get(1)));
    }

    @Test
    void testAccessToGetUserInfoShouldNotHaveAccess() throws Exception {
        mockMvc.perform(get("/user/{id}", userList.get(0).getId()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUserSuccessfullyShouldReturnUser() throws Exception {
        WebUser user = userCreation("email3", "password");
        user.setId(1);

        mockMvc.perform(put("/user")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new UserRegistration(user)))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
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

        mockMvc.perform(put("/user")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new UserRegistration(user)))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("app.user.error.did.not.changed"));
    }

    @Test
    void updateUserWithEmailThatAlreadyRegisteredShouldReturnUser() throws Exception {
        WebUser user = userCreation("email2", DECODE_PASSWORD);
        user.setId(1);

        mockMvc.perform(put("/user")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new UserRegistration(user)))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("app.user.error.email.already.exists"));
    }

    @Test
    void addGameToUserListOfGamesShouldAddToRepository() throws Exception {
        mockMvc.perform(post("/user/games")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(1))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(userList.get(0).getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist());
                //.andExpect(jsonPath("$.gameList").isArray())
                //.andExpect(jsonPath("$.gameList", hasSize(1)))
                //.andExpect(jsonPath("$.gameList[0].id").value(1))
                //.andExpect(jsonPath("$.gameList[0].name").value(games.get(0).getName()))
                //.andExpect(jsonPath("$.gameList[0].genre").value(games.get(0).getGenre().name()));
    }

    SecurityContextImpl getSecurityContext(){
        UserDto userDto = new UserDto(userList.get(0));
        userDto.setId(1);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userDto, userList.get(0).getPassword(), new ArrayList<>());
        return new SecurityContextImpl(token);
    }

}
