package com.gpb.common.service.impl;

import com.gpb.common.exception.BadRequestException;
import com.gpb.common.exception.ConflictRequestException;
import com.gpb.common.exception.NotFoundException;
import com.gpb.common.exception.RestTemplateRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RestTemplateHandlerServiceImplTest {

    private static final String GAME_SERVICE_URL = "http://example.com";

    private RestTemplate restTemplate = mock(RestTemplate.class);

    private RestTemplateHandlerServiceImpl restTemplateHandlerServiceImpl = new RestTemplateHandlerServiceImpl(restTemplate,
            "test-api-key",
            "http://example.com");

    @Test
    void testExecuteRequest_whenSuccessfulResponse_shouldReturnBody() {
        String url = "/resource";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Custom-Header", "value");
        String expectedResponse = "response-body";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);


        String result = restTemplateHandlerServiceImpl.executeRequest(url, method, headers, String.class);


        assertEquals(expectedResponse, result);
        verify(restTemplate, times(1)).exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void testExecuteRequestWithBody_whenSuccessfulResponse_shouldReturnBody() {
        String url = "/resource";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Custom-Header", "value");
        String expectedResponse = "response-body";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);


        String result = restTemplateHandlerServiceImpl.executeRequestWithBody(url, method, headers, "", String.class);


        assertEquals(expectedResponse, result);
        verify(restTemplate, times(1)).exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void executeRequest_whenNotFound_shouldThrowsNotFoundException() {
        String url = "/resource";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        HttpClientErrorException exception = new HttpClientErrorException("404 : \"app.game.error.id.not.found\"",
                HttpStatus.NOT_FOUND, "", null, null, null);

        when(restTemplate.exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class))).thenThrow(exception);


        NotFoundException result = assertThrows(NotFoundException.class, () ->
                restTemplateHandlerServiceImpl.executeRequest(url, method, headers, String.class)
        );


        assertNotNull(result);
        assertEquals("app.game.error.id.not.found",result.getMessage());
        verify(restTemplate, times(1)).exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void executeRequest_whenBadRequest_shouldThrowsBadRequestException() {
        String url = "/resource";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class))).thenThrow(exception);


        BadRequestException result = assertThrows(BadRequestException.class, () ->
                restTemplateHandlerServiceImpl.executeRequest(url, method, headers, String.class)
        );


        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void executeRequest_whenConflictRequest_shouldThrowsConflictRequestException() {
        String url = "/resource";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.CONFLICT);

        when(restTemplate.exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class))).thenThrow(exception);


        ConflictRequestException result = assertThrows(ConflictRequestException.class, () ->
                restTemplateHandlerServiceImpl.executeRequest(url, method, headers, String.class)
        );


        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void executeRequest_whenUnexpectedClientError_shouldThrowsRestTemplateRequestException() {
        String url = "/resource";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.LOCKED);

        when(restTemplate.exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class))).thenThrow(exception);


        RestTemplateRequestException result = assertThrows(RestTemplateRequestException.class, () ->
                restTemplateHandlerServiceImpl.executeRequest(url, method, headers, String.class)
        );


        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void executeRequest_whenServerError_shouldThrowsRestTemplateRequestException() {
        String url = "/resource";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT);

        when(restTemplate.exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class))).thenThrow(exception);


        RestTemplateRequestException result = assertThrows(RestTemplateRequestException.class, () ->
                restTemplateHandlerServiceImpl.executeRequest(url, method, headers, String.class)
        );


        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class));
    }
}