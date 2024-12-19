package com.gpb.telegram.service.impl;

import com.gpb.telegram.bean.TokenRequestDto;
import com.gpb.telegram.rest.RestTemplateHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLinkerServiceImplTest {


    @Mock
    private RestTemplateHandler restTemplateHandler;
    @InjectMocks
    private UserLinkerServiceImpl userService;

    @Test
    void testLinkAccounts_whenSuccess_shouldCallKafka() {
        String token = "token";
        long webUserId = 1L;
        long newBasicUserId = 1L;
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(webUserId));
        when(restTemplateHandler.executeRequestWithBody(
                "/user/link",
                HttpMethod.POST,
                headers,
                new TokenRequestDto(token),
                Long.class))
                .thenReturn(newBasicUserId);


        Long result = userService.linkAccounts(token, webUserId);


        assertEquals(newBasicUserId, result);
        verify(restTemplateHandler).executeRequestWithBody(
                "/user/link",
                HttpMethod.POST,
                headers,
                new TokenRequestDto(token),
                Long.class);
    }

    @Test
    void testGetAccountsLinkerToken_whenSuccess_shouldReturnToken() {
        String token = "token";
        long webUserId = 1L;
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(webUserId));
        when(restTemplateHandler
                .executeRequest("/user/token", HttpMethod.POST, headers, String.class))
                .thenReturn(token);


        String result = userService.getAccountsLinkerToken(webUserId);


        assertEquals(token, result);
        verify(restTemplateHandler).executeRequest("/user/token", HttpMethod.POST, headers, String.class);
    }
}
