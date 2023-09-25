package com.gpb.web.repository;

import com.gpb.web.bean.WebUser;
import org.springframework.data.repository.CrudRepository;

public interface WebUserRepository extends CrudRepository<WebUser, Long> {


    WebUser findById(long userId);

    WebUser findByEmail(String email);

    WebUser save(WebUser user);
}
