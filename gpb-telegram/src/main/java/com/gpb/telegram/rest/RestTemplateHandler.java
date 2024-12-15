package com.gpb.telegram.rest;

import com.gpb.telegram.exception.NotFoundException;
import com.gpb.telegram.exception.RestTemplateRequestException;
import com.gpb.telegram.util.Constants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Handle call of other service (use only for synchronize requests)
 */
@Slf4j
@Component
@Data
public class RestTemplateHandler {

    private final RestTemplate restTemplate;

    @Value("${API_KEY}")
    private String validApiKey;

    @Value("${GAME_SERVICE_URL}")
    private String gameServiceUrl;

    public RestTemplateHandler(RestTemplate template) {
        this.restTemplate = template;
    }

    public <T> T executeRequest(String url, HttpMethod httpMethod, HttpHeaders headers, Class<T> responseType) {
        url = gameServiceUrl + url;
        log.info("Request ({}){} with headers {}", httpMethod, url, headers);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(Constants.API_KEY_HEADER, validApiKey);
        if (headers != null) {
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



