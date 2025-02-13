package com.gpb.backend.service.impl;

import com.gpb.backend.entity.RefreshToken;
import com.gpb.backend.repository.RefreshTokenRepository;
import com.gpb.backend.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Optional<RefreshToken> getByToken(String token) {
        return refreshTokenRepository.findById(token);
    }

    @Override
    public void removeRefreshToken(String token) {
        refreshTokenRepository.deleteById(token);
    }

    @Override
    public RefreshToken createToken(RefreshToken refreshToken) {
        refreshTokenRepository.deleteByUserId(refreshToken.getUser().getId());
        return refreshTokenRepository.save(refreshToken);
    }
}
