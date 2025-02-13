package com.gpb.backend.repository;

import com.gpb.backend.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    void deleteByUserId(Long userId);
}
