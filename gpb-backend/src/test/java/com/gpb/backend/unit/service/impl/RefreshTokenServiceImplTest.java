package com.gpb.backend.unit.service.impl;

import com.gpb.backend.entity.RefreshToken;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.repository.RefreshTokenRepository;
import com.gpb.backend.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void testGetByToken_whenSuccess_shouldReturnRefreshToken() {
        String token = "token";
        Optional<RefreshToken> refreshToken = Optional.of(new RefreshToken());
        when(refreshTokenRepository.findById(token)).thenReturn(refreshToken);


        Optional<RefreshToken> result = refreshTokenService.getByToken(token);


        assertEquals(refreshToken, result);
    }

    @Test
    void testRemoveRefreshToken_whenSuccess_shouldRemoveToken() {
        String token = "token";


        refreshTokenService.removeRefreshToken(token);


        verify(refreshTokenRepository).deleteById(token);
    }

    @Test
    void testCreateToken_whenSuccess_shouldRemoveUserTokenAndSave() {
        String token = "token";
        long userId = 1L;
        WebUser user = WebUser.builder().id(userId).build();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .build();
        RefreshToken newToken = new RefreshToken();
        when(refreshTokenRepository.save(refreshToken)).thenReturn(newToken);


        RefreshToken result = refreshTokenService.createToken(refreshToken);


        assertEquals(result, newToken);
        verify(refreshTokenRepository).deleteByUserId(userId);
        verify(refreshTokenRepository).save(refreshToken);
    }
}
