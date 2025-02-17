package com.gpb.backend.repository;

import com.gpb.backend.entity.EmailChanging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailChangingRepository extends JpaRepository<EmailChanging, Long> {

    Optional<EmailChanging> findByOldEmailToken(String oldEmailToken);

    Optional<EmailChanging> findByNewEmailToken(String newEmailToken);

    void deleteByUserId(Long userId);
}
