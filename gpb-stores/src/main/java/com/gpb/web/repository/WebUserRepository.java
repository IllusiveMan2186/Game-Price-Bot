package com.gpb.web.repository;

import com.gpb.web.bean.user.WebUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WebUserRepository extends CrudRepository<WebUser, Long> {

    List<WebUser> findAllByIdIn(List<Long> userIds);
}