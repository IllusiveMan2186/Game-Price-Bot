package com.gpb.backend.configuration.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gpb.backend.entity.RefreshToken;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.exception.RefreshTokenException;
import com.gpb.backend.service.RefreshTokenService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.util.Constants;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class UserAuthenticationProvider {

    private final UserAuthenticationService userService;
    private final RefreshTokenService refreshTokenService;
    private final ModelMapper mapper;

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.refresh-token.secret-key}")
    private String refreshSecretKey;

    public UserAuthenticationProvider(UserAuthenticationService userService,
                                      RefreshTokenService refreshTokenService,
                                      ModelMapper mapper) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.mapper = mapper;
    }

    @PostConstruct
    protected void init() {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        this.refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates an Access Token for a given user ID.
     */
    public String generateAccessToken(Long userId) {
        return generateToken(userId, secretKey, Constants.TOKEN_EXPIRATION);
    }

    /**
     * Generates a Refresh Token for a given user.
     */
    public String generateRefreshToken(WebUser user) {
        String refreshToken = generateToken(user.getId(), refreshSecretKey, Constants.REFRESH_TOKEN_EXPIRATION);

        log.debug("Created refresh token for user {}", user.getId());

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusSeconds(Constants.REFRESH_TOKEN_EXPIRATION / 1000);

        RefreshToken tokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .user(user)
                .build();

        return refreshTokenService.createToken(tokenEntity).getToken();
    }

    /**
     * Validates and extracts user authentication from an access token.
     */
    public Authentication validateAuthToken(String token) {
        try {
            token = token.replaceFirst(Constants.AUTHORIZATION_HEADER_BEARER, "").trim();
            DecodedJWT decoded = verifyToken(token, secretKey);
            long userId = Long.parseLong(decoded.getSubject());

            UserDto user = userService.getUserById(userId);
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } catch (TokenExpiredException e) {
            log.warn("Access token expired");
            throw e;
        } catch (RuntimeException e) {
            log.error("Invalid access token: {}", e.getMessage());
            throw new SecurityException("Invalid access token", e);
        }
    }

    /**
     * Validates and extracts user information from a refresh token.
     */
    public UserDto validateRefreshToken(String refreshTokenValue) {
        try {
            verifyToken(refreshTokenValue, refreshSecretKey);
            RefreshToken refreshToken = refreshTokenService.getByToken(refreshTokenValue)
                    .orElseThrow(RefreshTokenException::new);

            return mapper.map(refreshToken.getUser(), UserDto.class);
        } catch (TokenExpiredException e) {
            log.warn("Refresh token expired");
            throw e;
        } catch (RuntimeException e) {
            log.error("Invalid refresh token: {}", e.getMessage());
            throw new SecurityException("Invalid refresh token", e);
        }
    }

    /**
     * Generates a JWT token with a given expiration.
     */
    private String generateToken(Long userId, String key, long expirationMillis) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        Algorithm algorithm = Algorithm.HMAC256(key);
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(algorithm);
    }

    /**
     * Verifies and decodes a JWT token using the provided key.
     */
    private DecodedJWT verifyToken(String token, String key) {
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}
