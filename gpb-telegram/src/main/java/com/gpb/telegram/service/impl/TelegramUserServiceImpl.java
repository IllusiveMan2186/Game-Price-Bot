package com.gpb.telegram.service.impl;

import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.repository.TelegramUserRepository;
import com.gpb.telegram.rest.RestTemplateHandler;
import com.gpb.telegram.service.TelegramUserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Slf4j
@Data
@AllArgsConstructor
public class TelegramUserServiceImpl implements TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;

    private final RestTemplateHandler restTemplateHandler;

    @Override
    public boolean isUserRegistered(long telegramId) {
        log.info(String.format("Check if user '%s' is registered", telegramId));
        return telegramUserRepository.existsByTelegramId(telegramId);
    }

    @Override
    public TelegramUser getUserById(long telegramId) {
        log.info(String.format("Get user by telegram id '%s'", telegramId));
        return telegramUserRepository.findByTelegramId(telegramId);
    }

    @Override
    public TelegramUser createTelegramUser(TelegramUser newUser) {
        log.info(String.format("New user '%s' registered", newUser.getTelegramId()));
        Long basicUserId = restTemplateHandler.executeRequest("/user", HttpMethod.POST, null, Long.class);
        newUser.setBasicUserId(basicUserId);
        return telegramUserRepository.save(newUser);
    }

    @Override
    public Locale changeUserLocale(long telegramId, Locale newLocale) {
        log.info(String.format("Change locale for user '%s'", telegramId));
        TelegramUser telegramUser = telegramUserRepository.findByTelegramId(telegramId);
        telegramUser.setLocale(newLocale);

        return telegramUserRepository.save(telegramUser).getLocale();
    }

    @Override
    public Locale getUserLocale(long telegramId) {
        return telegramUserRepository.findByTelegramId(telegramId).getLocale();
    }
}
