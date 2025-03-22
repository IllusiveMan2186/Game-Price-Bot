package com.gpb.game.unit.filter;

import com.gpb.common.util.CommonConstants;
import com.gpb.game.filter.ApiKeyFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyFilterTest {

    @InjectMocks
    private ApiKeyFilter apiKeyFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private static final String VALID_API_KEY = "test-api-key";

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws IOException {
        ReflectionTestUtils.setField(apiKeyFilter, "validApiKey", VALID_API_KEY);
    }

    @Test
    void doFilter_ValidApiKey_ProceedsWithFilterChain() throws ServletException, IOException {
        when(request.getHeader(CommonConstants.API_KEY_HEADER)).thenReturn(VALID_API_KEY);


        apiKeyFilter.doFilter(request, response, filterChain);


        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void doFilter_InvalidApiKey_ReturnsUnauthorized() throws ServletException, IOException {
        when(request.getHeader(CommonConstants.API_KEY_HEADER)).thenReturn("invalid-key");
        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);


        apiKeyFilter.doFilter(request, response, filterChain);


        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).getWriter();

        String responseContent = responseWriter.toString();
        assert responseContent.contains("Invalid API Key");

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilter_MissingApiKey_ReturnsUnauthorized() throws ServletException, IOException {
        when(request.getHeader(CommonConstants.API_KEY_HEADER)).thenReturn(null);
        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);


        apiKeyFilter.doFilter(request, response, filterChain);


        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).getWriter();

        String responseContent = responseWriter.toString();
        assert responseContent.contains("Invalid API Key");

        verify(filterChain, never()).doFilter(request, response);
    }
}
