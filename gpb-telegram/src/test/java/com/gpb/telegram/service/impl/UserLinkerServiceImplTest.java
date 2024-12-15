package com.gpb.telegram.service.impl;

import com.gpb.telegram.bean.event.AccountLinkerEvent;
import com.gpb.telegram.rest.RestTemplateHandler;
import com.gpb.telegram.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLinkerServiceImplTest {


    @Mock
    private KafkaTemplate<String, AccountLinkerEvent> kafkaTemplate;
    @Mock
    private RestTemplateHandler restTemplateHandler;
    @InjectMocks
    private UserLinkerServiceImpl userService;

    @Test
    void connectTelegramUser_shouldCallKafka() {
        String token = "token";
        long webUserId = 1L;
        AccountLinkerEvent event = new AccountLinkerEvent(token, webUserId);


        userService.linkAccounts(token, webUserId);


        verify(kafkaTemplate).send(eq(Constants.USER_SYNCHRONIZATION_ACCOUNTS_TOPIC), any(String.class), eq(event));
    }

    @Test
    void testGetAccountsLinkerToken_shouldReturnToken() {
        String token = "token";
        long webUserId = 1L;
        AccountLinkerEvent event = new AccountLinkerEvent(token, webUserId);
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
