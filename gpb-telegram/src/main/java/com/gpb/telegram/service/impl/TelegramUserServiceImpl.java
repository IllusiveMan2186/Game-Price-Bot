package com.gpb.telegram.service.impl;

import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.repository.TelegramUserRepository;
import com.gpb.telegram.service.TelegramUserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Slf4j
@AllArgsConstructor
public class TelegramUserServiceImpl implements TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;

    private final RestTemplateHandlerService restTemplateHandler;

    @Override
    public boolean isUserRegistered(long telegramId) {
        log.info("Check if user '{}' is registered", telegramId);
        return telegramUserRepository.existsByTelegramId(telegramId);
    }

    @Override
    public TelegramUser getUserById(long telegramId) {
        log.info("Get user by telegram id '{}'", telegramId);
        return telegramUserRepository.findByTelegramId(telegramId);
    }

    @Override
    @Transactional
    public TelegramUser createTelegramUser(TelegramUser newUser) {
        log.info("New user '{}' registered", newUser.getTelegramId());
        Long basicUserId = restTemplateHandler.executeRequest("/user", HttpMethod.POST, null, Long.class);
        newUser.setBasicUserId(basicUserId);
        return telegramUserRepository.save(newUser);
    }

    @Override
    public Locale changeUserLocale(long telegramId, Locale newLocale) {
        log.info("Change locale for user '{}'", telegramId);
        TelegramUser telegramUser = telegramUserRepository.findByTelegramId(telegramId);
        telegramUser.setLocale(newLocale);

        return telegramUserRepository.save(telegramUser).getLocale();
    }

    @Override
    public Locale getUserLocale(long telegramId) {
        return telegramUserRepository.findByTelegramId(telegramId).getLocale();
    }

    @Override
    public void setBasicUserId(long currentBasicUserId, long newBasicUserId) {
        telegramUserRepository.updateBasicUserIdByBasicUserId(currentBasicUserId, newBasicUserId);
    }
}
