package com.gpb.backend.unit.rest;

import com.gpb.backend.exception.NotFoundException;
import com.gpb.backend.exception.RestTemplateRequestException;
import com.gpb.backend.rest.RestTemplateHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RestTemplateHandlerTest {

    private static final String GAME_SERVICE_URL = "http://example.com";
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestTemplateHandler restTemplateHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restTemplateHandler.setValidApiKey("test-api-key");
        restTemplateHandler.setGameServiceUrl("http://example.com");
    }

    @Test
    void testExecuteRequest_whenSuccessfulResponse_shouldReturnBody() {
        String url = "/resource";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Custom-Header", "value");
        String expectedResponse = "response-body";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);


        String result = restTemplateHandler.executeRequest(url, method, headers, String.class);


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
                restTemplateHandler.executeRequest(url, method, headers, String.class)
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
                restTemplateHandler.executeRequest(url, method, headers, String.class)
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
                restTemplateHandler.executeRequest(url, method, headers, String.class)
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
                restTemplateHandler.executeRequest(url, method, headers, String.class)
        );

        assertNotNull(exception);
        verify(restTemplate, times(1)).exchange(eq(GAME_SERVICE_URL + url), eq(method), any(HttpEntity.class), eq(String.class));
    }
}