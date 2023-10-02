package com.gpb.web.repository;

import com.gpb.web.bean.user.BasicUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<BasicUser, Long> {

    BasicUser findById(long userId);

    void deleteById(long id);
}
