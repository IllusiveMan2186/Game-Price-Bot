package com.gpb.telegram.repository;

import com.gpb.telegram.bean.BasicUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<BasicUser, Long> {

    BasicUser findById(long userId);

    void deleteById(long id);
}
