package com.gpb.backend.service;

import com.gpb.backend.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> getByToken(String token);

    void removeRefreshToken(String token);

    RefreshToken createToken(RefreshToken refreshToken);
}
