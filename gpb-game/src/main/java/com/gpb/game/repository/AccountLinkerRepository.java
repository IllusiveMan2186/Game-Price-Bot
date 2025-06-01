package com.gpb.game.repository;

import com.gpb.game.entity.user.AccountLinker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountLinkerRepository extends JpaRepository<AccountLinker, String> {

    void deleteByUserId(long userId);
}
