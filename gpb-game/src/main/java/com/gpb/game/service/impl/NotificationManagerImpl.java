package com.gpb.game.service.impl;

import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.resolver.NotificationServiceResolver;
import com.gpb.game.service.NotificationManager;
import com.gpb.game.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationManagerImpl implements NotificationManager {

    private final NotificationServiceResolver notificationFactory;
    private final ModelMapper mapper;

    @Override
    public void sendGameInfoChange(BasicUser user, List<GameInShop> gameInShopList) {
        log.info("Send to user({}) list of ({}) changed games", user.getId(), gameInShopList.size());

        List<GameInStoreDto> gameInStoreDtoList = gameInShopList.stream()
                .map(game -> mapper.map(game, GameInStoreDto.class))
                .toList();

        for (UserNotificationType type : user.getNotificationTypes()) {
            log.info("Send to user({}) by notification type: {}", user.getId(), type);

            NotificationService service = notificationFactory.getService(type);
            service.sendGameInfoChange(user, gameInStoreDtoList);
        }
    }
}
