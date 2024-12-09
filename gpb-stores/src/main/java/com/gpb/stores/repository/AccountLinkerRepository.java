package com.gpb.stores.repository;

import com.gpb.stores.bean.user.AccountLinker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountLinkerRepository extends CrudRepository<AccountLinker, String> {
}
