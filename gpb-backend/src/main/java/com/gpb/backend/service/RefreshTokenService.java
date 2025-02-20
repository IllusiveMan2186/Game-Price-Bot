package com.gpb.backend.service;

import com.gpb.backend.entity.RefreshToken;

import java.util.Optional;

/**
 * Service interface for managing refresh tokens.
 */
public interface RefreshTokenService {

    /**
     * Retrieves a refresh token by its token string.
     *
     * @param token the refresh token string
     * @return an Optional containing the refresh token if found
     */
    Optional<RefreshToken> getByToken(String token);

    /**
     * Removes a refresh token by its token string.
     *
     * @param token the refresh token string to remove
     */
    void removeRefreshToken(String token);

    /**
     * Creates a new refresh token.
     *
     * @param refreshToken the refresh token entity to create
     * @return the created refresh token
     */
    RefreshToken createToken(RefreshToken refreshToken);
}
