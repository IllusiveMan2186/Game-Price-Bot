package com.gpb.game.service.impl;

import com.gpb.game.bean.game.GameInShop;
import com.gpb.game.bean.user.BasicUser;
import com.gpb.game.bean.user.UserNotificationType;
import com.gpb.game.service.NotificationManager;
import com.gpb.game.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificationManagerImpl implements NotificationManager {

    private final Map<String, NotificationService> notificationServices;

    public NotificationManagerImpl(Map<String, NotificationService> notificationServices) {
        this.notificationServices = notificationServices;
    }

    @Override
    public void sendGameInfoChange(BasicUser user, List<GameInShop> gameInShopList) {
        log.info("Send to user({}) list of ({}) changed games", user.getId(), gameInShopList.size());
        for (UserNotificationType notificationType : user.getNotificationTypes()) {
            log.info("Send to user({}) list of changed games by ({}) notification type ", user.getId(), notificationType);
            notificationServices.get(notificationType.name()).sendGameInfoChange(user, gameInShopList);
        }
    }
}
