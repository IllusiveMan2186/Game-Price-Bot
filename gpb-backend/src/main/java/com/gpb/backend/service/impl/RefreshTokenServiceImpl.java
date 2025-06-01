package com.gpb.backend.service.impl;

import com.gpb.backend.entity.RefreshToken;
import com.gpb.backend.repository.RefreshTokenRepository;
import com.gpb.backend.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Optional<RefreshToken> getByToken(String token) {
        log.debug("Get refresh token");
        return refreshTokenRepository.findById(token);
    }

    @Override
    public void removeRefreshToken(String token) {
        log.debug("Remove refresh token");
        refreshTokenRepository.deleteById(token);
    }

    @Override
    public RefreshToken createToken(RefreshToken refreshToken) {
        log.debug("Create new refresh token");
        refreshTokenRepository.deleteByUserId(refreshToken.getUser().getId());
        refreshTokenRepository.flush();
        return refreshTokenRepository.save(refreshToken);
    }
}
