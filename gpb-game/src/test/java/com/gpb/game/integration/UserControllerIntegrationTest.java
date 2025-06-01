package com.gpb.game.integration;

import com.gpb.common.entity.user.NotificationRequestDto;
import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.common.util.CommonConstants;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends BaseControllersIntegration {

    @Test
    void testUserCreation_whenSuccess_shouldReturnUserId() throws Exception {

        mockMvc.perform(post("/user")
                        .contentType(APPLICATION_JSON)
                        .header(CommonConstants.API_KEY_HEADER, API_KEY)
                        .content(objectToJson(new NotificationRequestDto(UserNotificationType.EMAIL))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void testUserAccountLinkerCreation_whenSuccess_shouldReturnToken() throws Exception {
        mockMvc.perform(post("/user")
                        .contentType(APPLICATION_JSON)
                        .header(CommonConstants.API_KEY_HEADER, API_KEY)
                        .content(objectToJson(new NotificationRequestDto(UserNotificationType.EMAIL))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1));

        mockMvc.perform(post("/user/token")
                        .header(CommonConstants.API_KEY_HEADER, API_KEY)
                        .header(CommonConstants.BASIC_USER_ID_HEADER, 1))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(matchesPattern("^[a-fA-F0-9\\-]{36}$")));
    }
}
