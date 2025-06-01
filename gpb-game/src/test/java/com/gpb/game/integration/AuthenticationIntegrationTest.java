package com.gpb.game.integration;

import com.gpb.common.util.CommonConstants;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationIntegrationTest extends BaseControllersIntegration {

    @Test
    void testRequestFilter_whenApiKeyMissing_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/game/{id}", games.get(0).getId())
                        .header(CommonConstants.BASIC_USER_ID_HEADER, -1))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
