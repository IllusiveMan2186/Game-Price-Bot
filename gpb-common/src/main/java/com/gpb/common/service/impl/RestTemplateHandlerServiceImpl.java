package com.gpb.common.service.impl;

import com.gpb.common.exception.BadRequestException;
import com.gpb.common.exception.ConflictRequestException;
import com.gpb.common.exception.NotFoundException;
import com.gpb.common.exception.RestTemplateRequestException;
import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.util.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@AllArgsConstructor
public class RestTemplateHandlerServiceImpl implements RestTemplateHandlerService {

    private final RestTemplate restTemplate;

    private String validApiKey;

    private String gameServiceUrl;


    public <T> T executeRequest(String url,
                                HttpMethod httpMethod,
                                HttpHeaders headers,
                                Class<T> responseType) {
        return executeRequestWithBody(url, httpMethod, headers, null, responseType);
    }

    public <T> T executeRequestWithBody(String url,
                                        HttpMethod httpMethod,
                                        HttpHeaders headers,
                                        Object requestBody,
                                        Class<T> responseType) {
        url = gameServiceUrl + url;
        log.debug("Request ({})'{}' with headers '{}' and body '{}'", httpMethod, url, headers, requestBody);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CommonConstants.API_KEY_HEADER, validApiKey);
        if (headers != null) {
            log.debug("Set headers for request");
            httpHeaders.addAll(headers);
        }
        HttpEntity<Object> entity;
        if (requestBody == null) {
            log.debug("Create entity without body");
            entity = new HttpEntity<>(httpHeaders);
        } else {
            log.debug("Create entity with body");
            entity = new HttpEntity<>(requestBody, httpHeaders);
        }
        try {
            log.debug("Sending request to main service");
            ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, entity, responseType);
            log.debug("Get response for request");
            return response.getBody();

        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode().is4xxClientError()) {
                log.warn("For request '{}' Error: '{}' : {}", url, exception.getMessage(), exception);
                handleClientError(exception);
            }
            log.error("For request '{}' Error: '{}' : {}", url, exception.getMessage(), exception);
            throw new RestTemplateRequestException(exception);
        }
    }

    private void handleClientError(HttpClientErrorException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String message = extractMessage(exception.getMessage());
        switch (status) {
            case NOT_FOUND -> throw new NotFoundException(message);
            case BAD_REQUEST -> throw new BadRequestException(message);
            case CONFLICT -> throw new ConflictRequestException(message);
            default -> throw new RestTemplateRequestException(exception);
        }
    }

    private String extractMessage( String fullMessage){
        Pattern pattern = Pattern.compile("^(\\d{3})\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(fullMessage);

        if (matcher.find()) {
            return matcher.group(2);
        }
        return "";
    }
}



