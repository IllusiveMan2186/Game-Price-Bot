package com.gpb.game.service.impl;

import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.service.NotificationManager;
import com.gpb.game.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationManagerImpl implements NotificationManager {

    private final Map<String, NotificationService> notificationServices;
    private final ModelMapper mapper;

    @Override
    public void sendGameInfoChange(BasicUser user, List<GameInShop> gameInShopList) {
        log.info("Send to user({}) list of ({}) changed games", user.getId(), gameInShopList.size());
        for (UserNotificationType notificationType : user.getNotificationTypes()) {
            log.info("Send to user({}) list of changed games by ({}) notification type ", user.getId(), notificationType);

            List<GameInStoreDto> gameInStoreDtoList = gameInShopList.stream()
                    .map(game -> mapper.map(game, GameInStoreDto.class))
                    .toList();

            notificationServices.get(notificationType.name()).sendGameInfoChange(user, gameInStoreDtoList);
        }
    }
}
