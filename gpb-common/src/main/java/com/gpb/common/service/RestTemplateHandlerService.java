package com.gpb.common.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * Service interface for handling HTTP requests using RestTemplate.
 * <p>
 * This interface provides methods to execute HTTP requests with or without request bodies,
 * allowing for flexible communication with external services.
 */
public interface RestTemplateHandlerService {

    /**
     * Executes an HTTP request without a request body.
     *
     * @param url          the URL of the target service
     * @param httpMethod   the HTTP method (e.g., GET, POST, etc.)
     * @param headers      the HTTP headers for the request
     * @param responseType the class of the response entity
     * @param <T>          the type of the response
     * @return the response of the specified type
     */
    <T> T executeRequest(String url,
                         HttpMethod httpMethod,
                         HttpHeaders headers,
                         Class<T> responseType);

    /**
     * Executes an HTTP request with a request body.
     *
     * @param url          the URL of the target service
     * @param httpMethod   the HTTP method (e.g., POST, PUT, etc.)
     * @param headers      the HTTP headers for the request
     * @param requestBody  the body of the request
     * @param responseType the class of the response entity
     * @param <T>          the type of the response
     * @return the response of the specified type
     */
    <T> T executeRequestWithBody(String url,
                                 HttpMethod httpMethod,
                                 HttpHeaders headers,
                                 Object requestBody,
                                 Class<T> responseType);
}
