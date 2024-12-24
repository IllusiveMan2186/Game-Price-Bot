package com.gpb.backend.integration.game;

import com.gpb.backend.entity.dto.EmailRequestDto;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends BaseAuthenticationIntegration {


    @Test
    void testAccess_whenRequestGetUserInfo_shouldNotHaveAccess() throws Exception {
        mockMvc.perform(get("/user/{id}", userList.get(0).getId()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUser_whenSuccess_shouldReturnUser() throws Exception {
        String email = "email3";

        mockMvc.perform(put("/user/email")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new EmailRequestDto(email)))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void testUpdateUser_whenNotChangedInfo_shouldReturnErrorMessage() throws Exception {

        mockMvc.perform(put("/user/email")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new EmailRequestDto(userList.get(0).getEmail())))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("app.user.error.did.not.changed"));
    }

}
