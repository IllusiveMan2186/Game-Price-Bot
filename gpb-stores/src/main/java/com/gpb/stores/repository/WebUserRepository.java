package com.gpb.stores.repository;

import com.gpb.stores.bean.user.WebUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WebUserRepository extends CrudRepository<WebUser, Long> {

    List<WebUser> findAllByIdIn(List<Long> userIds);
}