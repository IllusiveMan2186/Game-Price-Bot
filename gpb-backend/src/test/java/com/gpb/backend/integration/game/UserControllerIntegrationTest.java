package com.gpb.backend.integration.game;

import com.gpb.backend.entity.dto.LocaleRequestDto;
import com.gpb.backend.entity.dto.PasswordChangeDto;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends BaseIntegration {

    @Test
    void testAccess_whenRequestGetUserInfo_shouldNotHaveAccess() throws Exception {
        mockMvc.perform(get("/user/{id}", userList.get(0).getId()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testPasswordChange_whenSuccess_shouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/user/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new PasswordChangeDto(userList.get(0).getPassword().toCharArray(), "newPassword".toCharArray())))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void testLocaleChange_whenSuccess_shouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/user/locale")
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new LocaleRequestDto("ru")))
                        .sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContext()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
