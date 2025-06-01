package com.gpb.backend.repository;

import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.WebUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivationRepository extends JpaRepository<UserActivation, String> {

    UserActivation findByToken(String token);

    UserActivation findByUser(WebUser user);
}
