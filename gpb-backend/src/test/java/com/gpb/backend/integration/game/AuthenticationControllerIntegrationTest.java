package com.gpb.backend.integration.game;

import com.gpb.backend.entity.Credentials;
import com.gpb.backend.entity.UserRegistration;
import com.gpb.backend.util.Constants;
import com.gpb.common.entity.user.NotificationRequestDto;
import com.gpb.common.entity.user.UserNotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
                .thenReturn(2L);
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
    void testLogin_whenCookiesDisabled_shouldReturnUserInfoWithToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new Credentials(userList.get(0).getEmail(), DECODE_PASSWORD.toCharArray(), false))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userList.get(0).getId()))
                .andExpect(jsonPath("$.email").value(userList.get(0).getEmail()))
                .andReturn();


        String setCookieHeader = result.getResponse().getHeader("Set-Cookie");


        assertNull(setCookieHeader, "Set-Cookie header should not be present in the response");
        ;
    }

    @Test
    void testLogin_whenCookiesEnabled_shouldReturnUserInfoWithTokenInCookies() throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new Credentials(userList.get(0).getEmail(), DECODE_PASSWORD.toCharArray(), true))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userList.get(0).getId()))
                .andExpect(jsonPath("$.email").value(userList.get(0).getEmail()))
                .andReturn();


        String setCookieHeader = result.getResponse().getHeader("Set-Cookie");


        assertNotNull(setCookieHeader, "Set-Cookie header should be present in the response");
        assertTrue(setCookieHeader.contains(Constants.TOKEN_COOKIES_ATTRIBUTE + "="), "Auth token cookie should be set");
    }

}
