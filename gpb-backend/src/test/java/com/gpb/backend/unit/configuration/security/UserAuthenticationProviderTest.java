package com.gpb.backend.unit.configuration.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.entity.RefreshToken;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.exception.GpbTokenExpireException;
import com.gpb.backend.service.RefreshTokenService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationProviderTest {

    @Mock
    private UserAuthenticationService userService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private UserAuthenticationProvider userAuthenticationProvider;

    private final String secretKey = Base64.getEncoder().encodeToString("test-secret-key".getBytes(StandardCharsets.UTF_8));
    private final String refreshSecretKey = Base64.getEncoder().encodeToString("test-refresh-key".getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    void setUp() throws Exception {
        userAuthenticationProvider = new UserAuthenticationProvider(userService, refreshTokenService, mapper);

        setPrivateField(userAuthenticationProvider, "secretKey", secretKey);

        setPrivateField(userAuthenticationProvider, "refreshSecretKey", refreshSecretKey);
    }

    @Test
    void testGenerateAccessToken() {
        Long userId = 1L;
        String token = userAuthenticationProvider.generateAccessToken(userId);
        assertNotNull(token);
    }

    @Test
    void testGenerateRefreshToken() {
        WebUser user = new WebUser();
        user.setId(1L);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("test-refresh-token");
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(Constants.REFRESH_TOKEN_EXPIRATION / 1000));
        refreshToken.setUser(user);

        when(refreshTokenService.createToken(any(RefreshToken.class))).thenReturn(refreshToken);

        String token = userAuthenticationProvider.generateRefreshToken(user);
        assertNotNull(token);
        assertEquals("test-refresh-token", token);
    }

    @Test
    void testValidateAuthToken_ValidToken() {
        Long userId = 1L;
        UserDto userDto = mock(UserDto.class);

        when(userService.getUserById(userId)).thenReturn(userDto);

        String token = userAuthenticationProvider.generateAccessToken(userId);
        Authentication authentication = userAuthenticationProvider.validateAuthToken(token);

        assertNotNull(authentication);
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
    }

    @Test
    void testValidateAuthToken_ExpiredToken() {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String expiredToken = JWT.create()
                .withSubject(String.valueOf(1L))
                .withIssuedAt(new Date(now.getTime() - 10000))
                .withExpiresAt(new Date(now.getTime() - 10000))
                .sign(algorithm);

        assertThrows(GpbTokenExpireException.class, () -> userAuthenticationProvider.validateAuthToken(expiredToken));
    }

    @Test
    void testValidateAuthToken_whenInvalidToken_shouldThrowSecurityException() {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String invalidToken = JWT.create()
                .withIssuedAt(new Date(now.getTime()))
                .withExpiresAt(new Date(now.getTime() + 10000))
                .sign(algorithm);

        assertThrows(SecurityException.class, () -> userAuthenticationProvider.validateAuthToken(invalidToken));
    }

    @Test
    void testValidateRefreshToken_whenValidToken() {
        Long userId = 1L;
        Date now = new Date();
        WebUser user = new WebUser();
        RefreshToken refreshToken = RefreshToken.builder().user(user).build();
        Algorithm algorithm = Algorithm.HMAC256(refreshSecretKey);
        String token = JWT.create()
                .withSubject(String.valueOf(userId))
                .withIssuedAt(new Date(now.getTime() - 10000))
                .withExpiresAt(new Date(now.getTime() + 10000))
                .sign(algorithm);
        UserDto userDto = mock(UserDto.class);

        when(refreshTokenService.getByToken(token)).thenReturn(Optional.of(refreshToken));
        when(mapper.map(user, UserDto.class)).thenReturn(userDto);


        UserDto result = userAuthenticationProvider.validateRefreshToken(token);


        assertEquals(userDto, result);
    }

    @Test
    void testValidateAuthToken_whenExpiredToken_shouldThrowException() {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(refreshSecretKey);
        String expiredToken = JWT.create()
                .withSubject(String.valueOf(1L))
                .withIssuedAt(new Date(now.getTime() - 11000))
                .withExpiresAt(new Date(now.getTime() - 10000))
                .sign(algorithm);

        assertThrows(GpbTokenExpireException.class, () -> userAuthenticationProvider.validateRefreshToken(expiredToken));
    }

    @Test
    void testValidateRefreshToken_whenInvalidToken_shouldThrowSecurityException() {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(refreshSecretKey);
        String invalidToken = JWT.create()
                .withIssuedAt(new Date(now.getTime()))
                .withExpiresAt(new Date(now.getTime() + 10000))
                .sign(algorithm);

        assertThrows(SecurityException.class, () -> userAuthenticationProvider.validateRefreshToken(invalidToken));
    }


    // Utility method to set private fields via reflection
    private void setPrivateField(Object object, String fieldName, String value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}

