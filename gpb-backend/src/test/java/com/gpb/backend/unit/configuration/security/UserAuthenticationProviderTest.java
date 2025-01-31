package com.gpb.backend.unit.configuration.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAuthenticationProviderTest {

    private UserAuthenticationProvider userAuthenticationProvider;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    private final String testSecretKey = "test-secret-key";
    private String encodedSecretKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        encodedSecretKey = Base64.getEncoder().encodeToString(testSecretKey.getBytes());

        userAuthenticationProvider = new UserAuthenticationProvider(userAuthenticationService);
        userAuthenticationProvider.setSecretKey(encodedSecretKey);
    }

    @Test
    void testCreateToken_whenSuccess_shouldCreateValidToken() {
        String login = "test@example.com";


        String token = userAuthenticationProvider.createToken(login);


        assertNotNull(token);
        String decodedLogin = JWT.decode(token).getSubject();
        assertEquals(login, decodedLogin);
    }

    @Test
    void testCreateToken_whenSuccess_shouldValidateTokenAndReturnAuthentication() {
        String login = "test@example.com";
        UserDto userDto = new UserDto("email","pass",null, Constants.USER_ROLE,"");
        userDto.setEmail(login);

        when(userAuthenticationService.getUserByEmail(login)).thenReturn(userDto);

        String token = userAuthenticationProvider.createToken(login);


        Authentication authentication = userAuthenticationProvider.validateToken(token);


        assertNotNull(authentication);
        assertEquals(login, ((UserDto) authentication.getPrincipal()).getEmail());
        verify(userAuthenticationService, times(1)).getUserByEmail(login);
    }

    @Test
    void testCreateToken_whenInvalidToken_shouldThrowExceptionWhenTokenIsInvalid() {
        String invalidToken = "invalid.jwt.token";


        assertThrows(JWTVerificationException.class, () -> userAuthenticationProvider.validateToken(invalidToken));
    }

    @Test
    void testCreateToken_whenTokenExpired_shouldThrowExceptionWhenTokenIsExpired() {
        String login = "test@example.com";
        Algorithm algorithm = Algorithm.HMAC256(encodedSecretKey);
        Date pastDate = new Date(System.currentTimeMillis() - 1000);

        String expiredToken = JWT.create()
                .withSubject(login)
                .withIssuedAt(pastDate)
                .withExpiresAt(pastDate)
                .sign(algorithm);


        assertThrows(JWTVerificationException.class, () -> userAuthenticationProvider.validateToken(expiredToken));
    }
}

