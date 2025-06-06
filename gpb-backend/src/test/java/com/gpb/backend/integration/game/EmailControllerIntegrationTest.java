package com.gpb.backend.integration.game;

import com.gpb.backend.entity.dto.EmailRequestDto;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmailControllerIntegrationTest  extends BaseIntegration {

    @Test
    void testUpdateUser_whenSuccess_shouldReturnUser() throws Exception {
        String email = "email3";


        mockMvc.perform(patch("/email")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new EmailRequestDto(email)))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateUser_whenNotChangedInfo_shouldReturnErrorMessage() throws Exception {

        mockMvc.perform(patch("/email")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new EmailRequestDto(userList.get(0).getEmail())))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("app.user.error.did.not.changed"));
    }

}
