package com.gpb.telegram.service.impl;

import com.gpb.telegram.bean.BasicUser;
import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.bean.WebMessengerConnector;
import com.gpb.telegram.exception.NotExistingMessengerActivationTokenException;
import com.gpb.telegram.repository.TelegramUserRepository;
import com.gpb.telegram.repository.UserRepository;
import com.gpb.telegram.repository.WebMessengerConnectorRepository;
import com.gpb.telegram.service.TelegramUserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Slf4j
@AllArgsConstructor
public class TelegramUserServiceImpl implements TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;
    private final WebMessengerConnectorRepository messengerConnectorRepository;
    private final UserRepository userRepository;

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
        BasicUser user = new BasicUser();
        userRepository.save(user);
        newUser.setBasicUser(user);
        return telegramUserRepository.save(newUser);
    }

    @Override
    @Transactional
    public void synchronizeTelegramUser(String token, long telegramId) {
        log.info(String.format("Synchronize user '%s' with web part", telegramId));

        WebMessengerConnector connector = messengerConnectorRepository.findById(token)
                .orElseThrow(NotExistingMessengerActivationTokenException::new);
        BasicUser user = userRepository.findById(connector.getUserId());
        TelegramUser telegramUser = telegramUserRepository.findByTelegramId(telegramId);
        BasicUser oldUSer = telegramUser.getBasicUser();

        user.getGameList().addAll(oldUSer.getGameList());
        user.getNotificationTypes().addAll(oldUSer.getNotificationTypes());
        telegramUser.setBasicUser(user);

        telegramUserRepository.save(telegramUser);
        messengerConnectorRepository.deleteById(token);
        userRepository.deleteById(oldUSer.getId());
    }

    @Override
    public String getWebUserConnectorToken(long telegramId) {
        log.info(String.format("Get synchronization token for user '%s'", telegramId));

        TelegramUser telegramUser = telegramUserRepository.findByTelegramId(telegramId);
        WebMessengerConnector webMessengerConnector = WebMessengerConnector.builder()
                .userId(telegramUser.getBasicUser().getId()).build();
        return messengerConnectorRepository.save(webMessengerConnector).getToken();
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

    @Override
    public void subscribeToGame(long telegramId, long gameId) {
        log.info(String.format("Subscribe for game(%s) into user(%s) game list", gameId, telegramId));
        TelegramUser telegramUser = telegramUserRepository.findByTelegramId(telegramId);
        userRepository.addGameToUserListOfGames(telegramUser.getBasicUser().getId(), gameId);
    }

    @Override
    public void unsubscribeFromGame(long telegramId, long gameId) {
        log.info(String.format("Unsubscribe game(%s) from user(%s) game list", gameId, telegramId));
        TelegramUser telegramUser = telegramUserRepository.findByTelegramId(telegramId);
        userRepository.removeGameFromUserListOfGames(telegramUser.getBasicUser().getId(), gameId);
    }
}
