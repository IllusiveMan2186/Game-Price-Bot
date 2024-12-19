package com.gpb.backend.service.impl;

import com.gpb.backend.bean.user.dto.TokenRequestDto;
import com.gpb.backend.rest.RestTemplateHandler;
import com.gpb.backend.service.UserLinkerService;
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
        return restTemplateHandler.executeRequestWithBody(
                "/user/link",
                HttpMethod.POST,
                headers,
                new TokenRequestDto(token),
                Long.class);
    }

    @Override
    public String getAccountsLinkerToken(long basicUserId) {
        log.info("Get account linker token for user {}", basicUserId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(basicUserId));
        return restTemplateHandler.executeRequest("/user/token", HttpMethod.POST, headers, String.class);
    }
}
