package com.gpb.telegram.repository;

import com.gpb.telegram.entity.TelegramUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramUserRepository extends CrudRepository<TelegramUser, Long> {

    boolean existsByTelegramId(long telegramId);
    TelegramUser findByTelegramId(long telegramId);
    TelegramUser findByBasicUserId(long basicUserId);

    @Modifying
    @Query("UPDATE TelegramUser w SET w.basicUserId = :newBasicUserId WHERE w.basicUserId = :currentBasicUserId")
    void updateBasicUserIdByBasicUserId(@Param("currentBasicUserId") long currentBasicUserId, @Param("newBasicUserId") long newBasicUserId);
}
