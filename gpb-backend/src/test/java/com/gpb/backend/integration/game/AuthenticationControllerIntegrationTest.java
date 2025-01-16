package com.gpb.backend.integration.game;

import com.gpb.backend.entity.Credentials;
import com.gpb.backend.entity.UserRegistration;
import com.gpb.common.entity.user.NotificationRequestDto;
import com.gpb.common.entity.user.UserNotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerIntegrationTest extends BaseAuthenticationIntegration {

    @Test
    void testCreateUser_whenUSedAlreadyRegisteredEmail_shouldReturnErrorMessage() throws Exception {
        UserRegistration userRegistration = UserRegistration.builder()
                .email(userList.get(0).getEmail())
                .password(userList.get(0).getPassword().toCharArray())
                .locale(userList.get(0).getLocale().getLanguage())
                .build();


        mockMvc.perform(post("/registration")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(userRegistration)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("app.user.error.email.already.exists"));
    }

    @Test
    void testCreateUser_shenSuccess_shouldReturnUser() throws Exception {
        when(restTemplateHandler
                .executeRequestWithBody(
                        "/user",
                        HttpMethod.POST,
                        null,
                        new NotificationRequestDto(UserNotificationType.EMAIL),
                        Long.class))
                .thenReturn(1L);
        UserRegistration userRegistration = UserRegistration.builder()
                .email("email2")
                .password("password".toCharArray())
                .locale("ua")
                .build();

        mockMvc.perform(post("/registration")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(userRegistration)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void testLogin_shenSuccess_shouldSetUserInfoInSession() throws Exception {
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
