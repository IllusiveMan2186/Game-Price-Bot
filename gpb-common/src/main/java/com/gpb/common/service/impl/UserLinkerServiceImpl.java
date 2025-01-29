package com.gpb.common.service.impl;

import com.gpb.common.entity.event.LinkUsersEvent;
import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.service.UserLinkerService;
import com.gpb.common.util.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class UserLinkerServiceImpl implements UserLinkerService {

    private final RestTemplateHandlerService templateHandlerService;
    private final KafkaTemplate<String, LinkUsersEvent> linkUsersEventKafkaTemplate;

    @Override
    public void linkAccounts(String token, long currentUserBasicId) {
        log.info("Link user {} by token {}", currentUserBasicId, token);
        String key = UUID.randomUUID().toString();
        LinkUsersEvent linkUsersEvent = new LinkUsersEvent(token, currentUserBasicId);
        linkUsersEventKafkaTemplate.send(CommonConstants.LINK_USERS_TOPIC, key, linkUsersEvent);
    }

    @Override
    public String getAccountsLinkerToken(long basicUserId) {
        log.info("Get account linker token for user {}", basicUserId);
        String url = "/user/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add(CommonConstants.BASIC_USER_ID_HEADER, String.valueOf(basicUserId));
        return templateHandlerService.executeRequest(url, HttpMethod.POST, headers, String.class);
    }
}
