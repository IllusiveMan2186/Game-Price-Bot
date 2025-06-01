package com.gpb.backend.repository;

import com.gpb.backend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    void deleteByUserId(Long userId);
}
