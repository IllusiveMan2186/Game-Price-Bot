package com.gpb.web.repository;

import com.gpb.web.bean.user.UserActivation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivationRepository extends CrudRepository<UserActivation, String> {

    UserActivation findByToken(String token);
}
