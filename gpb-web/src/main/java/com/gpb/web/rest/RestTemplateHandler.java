package com.gpb.web.rest;

import com.gpb.web.exception.NotFoundException;
import com.gpb.web.exception.RestTemplateRequestException;
import com.gpb.web.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class RestTemplateHandler {

    @Value("${API_KEY}")
    private String validApiKey;

    private final RestTemplate restTemplate;

    public RestTemplateHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T executeRequest(String url, HttpMethod httpMethod, HttpHeaders headers, Class<T> responseType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(Constants.API_KEY_HEADER, validApiKey);
        if(headers != null){
            httpHeaders.addAll(headers);
        }
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);

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
        } catch (Exception e) {
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



