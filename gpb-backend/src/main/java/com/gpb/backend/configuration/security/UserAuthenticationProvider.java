package com.gpb.backend.configuration.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.util.Constants;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

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
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

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

    public Authentication validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(token);

        UserDto user = userService.getUserByEmail(decoded.getSubject());

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

}
