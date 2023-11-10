package com.gpb.web.integration.game;

import com.gpb.web.bean.user.EmailChangeDto;
import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.WebUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        String email ="email3";
        EmailChangeDto emailChangeDto = new EmailChangeDto();
        emailChangeDto.setEmail(email);

        mockMvc.perform(put("/user/email")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(emailChangeDto))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void updateUserThatDidNotChangedInfoShouldReturnErrorMessage() throws Exception {
        EmailChangeDto emailChangeDto = new EmailChangeDto();
        emailChangeDto.setEmail(userList.get(0).getEmail());

        mockMvc.perform(put("/user/email")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(emailChangeDto))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("app.user.error.did.not.changed"));
    }

    @Test
    void updateUserWithEmailThatAlreadyRegisteredShouldReturnUser() throws Exception {
        String email ="email2";
        EmailChangeDto emailChangeDto = new EmailChangeDto();
        emailChangeDto.setEmail(email);

        mockMvc.perform(put("/user/email")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(emailChangeDto))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("app.user.error.email.already.exists"));
    }

    @Test
    void addGameToUserListOfGamesShouldAddToRepository() throws Exception {
        mockMvc.perform(post("/user/games/{gameId}", games.get(0).getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(1))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(userList.get(0).getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

}
