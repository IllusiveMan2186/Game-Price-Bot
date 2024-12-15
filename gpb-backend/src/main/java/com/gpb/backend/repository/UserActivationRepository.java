package com.gpb.backend.repository;

import com.gpb.backend.bean.user.UserActivation;
import com.gpb.backend.bean.user.WebUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivationRepository extends CrudRepository<UserActivation, String> {

    UserActivation findByToken(String token);

    UserActivation findByUser(WebUser user);
}
