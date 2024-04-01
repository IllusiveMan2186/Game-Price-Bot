package com.gpb.web.integration.game;

import com.gpb.web.bean.user.Credentials;
import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.WebUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AuthenticationControllerIntegrationTest extends BaseAuthenticationIntegration {


    @BeforeEach
    void userCreationBeforeAllTests() {
        userList.add(userCreation("email2", DECODE_PASSWORD));
        userService.createUser(new UserRegistration(userList.get(1)));
    }


    @Test
    void createUserWithAlreadyRegisteredEmailShouldReturnErrorMessage() throws Exception {
        mockMvc.perform(post("/registration")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new UserRegistration(userList.get(0)))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("app.user.error.email.already.exists"));
    }

    @Test
    void createUserSuccessfullyShouldReturnUser() throws Exception {
        WebUser user = userCreation("email3", "password");
        System.out.println(user.getLocale());

        mockMvc.perform(post("/registration")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new UserRegistration(user))))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void loginSuccessfullyShouldSetUserInfoInSession() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new Credentials(userList.get(0).getEmail(), DECODE_PASSWORD.toCharArray()))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userList.get(0).getId()))
                .andExpect(jsonPath("$.email").value(userList.get(0).getEmail()));
        ;
    }
}
