package com.gpb.telegram.service.impl;


import com.gpb.telegram.bean.event.AccountLinkerEvent;
import com.gpb.telegram.rest.RestTemplateHandler;
import com.gpb.telegram.service.UserLinkerService;
import com.gpb.telegram.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserLinkerServiceImpl implements UserLinkerService {

    private final KafkaTemplate<String, AccountLinkerEvent> userSyncAccountsEventKafkaTemplate;
    private final RestTemplateHandler restTemplateHandler;

    public UserLinkerServiceImpl(KafkaTemplate<String, AccountLinkerEvent> userSyncAccountsEventKafkaTemplate,
                                 RestTemplateHandler restTemplateHandler) {
        this.userSyncAccountsEventKafkaTemplate = userSyncAccountsEventKafkaTemplate;
        this.restTemplateHandler = restTemplateHandler;
    }

    @Override
    public void linkAccounts(String token, long webUserId) {
        log.info("Link user {} by token {}", webUserId, token);
        String key = UUID.randomUUID().toString();
        AccountLinkerEvent event = new AccountLinkerEvent(token, webUserId);
        userSyncAccountsEventKafkaTemplate.send(Constants.USER_SYNCHRONIZATION_ACCOUNTS_TOPIC, key, event);
    }

    @Override
    public String getAccountsLinkerToken(long webUserId) {
        log.info("Get account linker token for user {}", webUserId);
        String url = "/user/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(webUserId));
        return restTemplateHandler.executeRequest(url, HttpMethod.POST, headers, String.class);
    }
}
