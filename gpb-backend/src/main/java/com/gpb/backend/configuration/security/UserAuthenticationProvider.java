package com.gpb.backend.configuration.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.util.Constants;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

/**
 * Provider responsible for creating and validating JWT tokens for user authentication.
 */
@Slf4j
@Data
@Component
public class UserAuthenticationProvider {

    private final UserAuthenticationService userService;

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    public UserAuthenticationProvider(UserAuthenticationService userService) {
        this.userService = userService;
    }

    @PostConstruct
    protected void init() {
        this.secretKey = Base64.getEncoder().encodeToString(this.secretKey.getBytes());
    }

    /**
     * Creates a JWT token for the specified user login.
     *
     * @param login the user's login (or email) to be set as the token's subject
     * @return the generated JWT token as a String
     */
    public String createToken(String login) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + (Constants.TOKEN_EXPIRATION * 1000));

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(login)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(algorithm);
    }

    /**
     * Validates the provided JWT token and returns an {@link Authentication} object if the token is valid.
     * <p>
     * If the token is invalid or expired, a {@link JWTVerificationException} is thrown.
     * </p>
     *
     * @param token the JWT token to validate
     * @return an {@link Authentication} object representing the authenticated user
     * @throws JWTVerificationException if the token is invalid or expired
     */
    public Authentication validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decoded = verifier.verify(token);

            UserDto user = userService.getUserByEmail(decoded.getSubject());
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } catch (JWTVerificationException e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw e;
        }
    }
}