package com.gpb.common.service.impl;

import com.gpb.common.exception.NotFoundException;
import com.gpb.common.exception.RestTemplateRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void executeRequest_notFound_throwsNotFoundException() {
        String url = "/resource";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();

        ResponseEntity<String> responseEntity = new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);


        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                restTemplateHandlerServiceImpl.executeRequest(url, method, headers, String.class)
        );

        assertTrue(exception.getMessage().contains("Not Found"));
        verify(restTemplate, times(1)).exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void executeRequest_clientError_throwsRestTemplateRequestException() {
        String url = "/resource";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();

        ResponseEntity<String> responseEntity = new ResponseEntity<>("Client Error", HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);


        RestTemplateRequestException exception = assertThrows(RestTemplateRequestException.class, () ->
                restTemplateHandlerServiceImpl.executeRequest(url, method, headers, String.class)
        );

        assertNotNull(exception);
        verify(restTemplate, times(1)).exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void executeRequest_restClientException_throwsRestTemplateRequestException() {
        String url = "/resource";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();

        when(restTemplate.exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class))).thenThrow(new RestClientException("Error occurred"));


        RestTemplateRequestException exception = assertThrows(RestTemplateRequestException.class, () ->
                restTemplateHandlerServiceImpl.executeRequest(url, method, headers, String.class)
        );

        assertNotNull(exception);
        verify(restTemplate, times(1)).exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void executeRequest_unexpectedStatusCode_throwsRestTemplateRequestException() {
        String url = "/resource";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();

        ResponseEntity<String> responseEntity = new ResponseEntity<>("Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);


        RestTemplateRequestException exception = assertThrows(RestTemplateRequestException.class, () ->
                restTemplateHandlerServiceImpl.executeRequest(url, method, headers, String.class)
        );

        assertNotNull(exception);
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


        String result = restTemplateHandlerServiceImpl.executeRequestWithBody(url, method, headers, new Object(), String.class);


        assertEquals(expectedResponse, result);
        verify(restTemplate, times(1)).exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class));
    }
}