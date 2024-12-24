package com.gpb.game.repository;

import com.gpb.game.entity.user.AccountLinker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountLinkerRepository extends CrudRepository<AccountLinker, String> {

    void deleteByUserId(long userId);
}
