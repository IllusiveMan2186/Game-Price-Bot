package com.gpb.common.service.impl;

import com.gpb.common.entity.event.LinkUsersEvent;
import com.gpb.common.entity.user.TokenRequestDto;
import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.util.CommonConstants;
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
    private RestTemplateHandlerService restTemplateHandlerServiceImpl;

    @Mock
    private KafkaTemplate<String, LinkUsersEvent> linkUsersEventKafkaTemplate;
    @InjectMocks
    private UserLinkerServiceImpl userService;

    @Test
    void testLinkAccounts_whenSuccess_shouldCallKafka() {
        String token = "token";
        long webUserId = 1L;


        userService.linkAccounts(token, webUserId);


        LinkUsersEvent linkUsersEvent = new LinkUsersEvent(token, webUserId);
        verify(linkUsersEventKafkaTemplate).send(
                eq(CommonConstants.LINK_USERS_TOPIC),
                any(String.class),
                eq(linkUsersEvent));
    }

    @Test
    void testGetAccountsLinkerToken_whenSuccess_shouldReturnToken() {
        String token = "token";
        long webUserId = 1L;
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(webUserId));
        when(restTemplateHandlerServiceImpl
                .executeRequest("/user/token", HttpMethod.POST, headers, String.class))
                .thenReturn(token);


        String result = userService.getAccountsLinkerToken(webUserId);


        assertEquals(token, result);
        verify(restTemplateHandlerServiceImpl).executeRequest("/user/token", HttpMethod.POST, headers, String.class);
    }
}
