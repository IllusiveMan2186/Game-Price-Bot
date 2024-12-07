package com.gpb.stores.integration;

import com.gpb.stores.util.Constants;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends BaseAuthenticationIntegration{

    @Test
    void getUserByIdSuccessfullyShouldReturnId() throws Exception {

        mockMvc.perform(post("/user")
                        .header(Constants.API_KEY_HEADER, API_KEY))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1));
    }
}
