package com.gpb.telegram.service.impl;

import com.gpb.telegram.rest.RestTemplateHandler;
import com.gpb.telegram.service.UserLinkerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserLinkerServiceImpl implements UserLinkerService {

    private final RestTemplateHandler restTemplateHandler;

    @Override
    public Long linkAccounts(String token, long basicUserId) {
        log.info("Link user {} by token {}", basicUserId, token);
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(basicUserId));
        return restTemplateHandler.executeRequest("/user/link", HttpMethod.POST, headers, Long.class);
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
