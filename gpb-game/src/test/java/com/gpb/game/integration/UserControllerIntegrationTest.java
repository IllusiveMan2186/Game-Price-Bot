package com.gpb.game.integration;

import com.gpb.game.util.Constants;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends BaseAuthenticationIntegration{

    @Test
    void testUserCreation_whenSuccess_shouldReturnUserId() throws Exception {

        mockMvc.perform(post("/user")
                        .header(Constants.API_KEY_HEADER, API_KEY))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void testUserAccountLinkerCreation_whenSuccess_shouldReturnToken() throws Exception {
        mockMvc.perform(post("/user")
                        .header(Constants.API_KEY_HEADER, API_KEY))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1));

        mockMvc.perform(post("/user/token")
                        .header(Constants.API_KEY_HEADER, API_KEY)
                        .header(Constants.BASIC_USER_ID_HEADER, 1))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(matchesPattern("^[a-fA-F0-9\\-]{36}$")));
    }
}
