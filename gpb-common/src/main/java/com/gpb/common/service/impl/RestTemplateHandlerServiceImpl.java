package com.gpb.common.service.impl;

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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
        log.info("Request ({}){} with headers {} and body {}", httpMethod, url, headers, requestBody);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CommonConstants.API_KEY_HEADER, validApiKey);
        if (headers != null) {
            httpHeaders.addAll(headers);
        }
        HttpEntity<Object> entity;
        if (requestBody == null) {
            entity = new HttpEntity<>(httpHeaders);
        } else {
            entity = new HttpEntity<>(requestBody, httpHeaders);
        }
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, entity, responseType);
            HttpStatus statusCode = (HttpStatus) response.getStatusCode();

            if (statusCode.is2xxSuccessful()) {
                return response.getBody();
            } else if (statusCode.is4xxClientError()) {
                handleClientError(url, response, statusCode);
            }
            log.error("Unexpected HTTP status code: {} - {}", statusCode, response.getBody());
            throw new RestTemplateRequestException();
        } catch (RestClientException e) {
            log.error("Unexpected Error: {}", e.getMessage(), e);
            throw new RestTemplateRequestException(e);
        }
    }

    private <T> void handleClientError(String url, ResponseEntity<T> response, HttpStatus statusCode) {
        if (statusCode == HttpStatus.NOT_FOUND) {
            log.warn("Resource not found: {} - {}", url, response.getBody());
            throw new NotFoundException(response.toString());
        }
    }
}



