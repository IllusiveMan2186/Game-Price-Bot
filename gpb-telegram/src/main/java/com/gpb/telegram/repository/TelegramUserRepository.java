package com.gpb.telegram.repository;

import com.gpb.telegram.bean.TelegramUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramUserRepository extends CrudRepository<TelegramUser, Long> {

    boolean existsByTelegramId(long telegramId);
    TelegramUser findByTelegramId(long telegramId);
}
