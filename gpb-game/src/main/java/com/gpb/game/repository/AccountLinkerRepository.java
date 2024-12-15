package com.gpb.game.repository;

import com.gpb.game.bean.user.AccountLinker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountLinkerRepository extends CrudRepository<AccountLinker, String> {
}
