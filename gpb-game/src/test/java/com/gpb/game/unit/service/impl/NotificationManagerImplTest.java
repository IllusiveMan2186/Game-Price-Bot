package com.gpb.game.unit.service.impl;

import com.gpb.game.bean.game.GameInShop;
import com.gpb.game.bean.user.BasicUser;
import com.gpb.game.bean.user.UserNotificationType;
import com.gpb.game.service.NotificationService;
import com.gpb.game.service.impl.NotificationManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class NotificationManagerImplTest {

    @Mock
    private NotificationService emailNotificationService;
    @Mock
    private NotificationService smsNotificationService;

    private NotificationManagerImpl notificationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Map<String, NotificationService> notificationServices = new HashMap<>();
        notificationServices.put(UserNotificationType.EMAIL.name(), emailNotificationService);
        notificationServices.put(UserNotificationType.TELEGRAM.name(), smsNotificationService);
        notificationManager = new NotificationManagerImpl(notificationServices);
    }

    @Test
    void testSendGameInfoChange_SingleNotificationType() {
        BasicUser user = new BasicUser();
        user.setId(1);
        user.setNotificationTypes(Collections.singletonList(UserNotificationType.EMAIL));

        GameInShop game1 = new GameInShop();
        GameInShop game2 = new GameInShop();
        List<GameInShop> gameInShopList = Arrays.asList(game1, game2);


        notificationManager.sendGameInfoChange(user, gameInShopList);


        verify(emailNotificationService, times(1)).sendGameInfoChange(user, gameInShopList);
        verifyNoInteractions(smsNotificationService);
    }

    @Test
    void testSendGameInfoChange_MultipleNotificationTypes() {
        BasicUser user = new BasicUser();
        user.setId(2);
        user.setNotificationTypes(Arrays.asList(UserNotificationType.EMAIL, UserNotificationType.TELEGRAM));

        GameInShop game1 = new GameInShop();
        List<GameInShop> gameInShopList = Collections.singletonList(game1);


        notificationManager.sendGameInfoChange(user, gameInShopList);


        verify(emailNotificationService, times(1)).sendGameInfoChange(user, gameInShopList);
        verify(smsNotificationService, times(1)).sendGameInfoChange(user, gameInShopList);
    }

    @Test
    void testSendGameInfoChange_NoNotificationTypes() {
        BasicUser user = new BasicUser();
        user.setId(3);
        user.setNotificationTypes(Collections.emptyList());

        List<GameInShop> gameInShopList = Collections.emptyList();


        notificationManager.sendGameInfoChange(user, gameInShopList);


        verifyNoInteractions(emailNotificationService);
        verifyNoInteractions(smsNotificationService);
    }

    @Test
    void testSendGameInfoChange_NullNotificationType() {
        BasicUser user = new BasicUser();
        user.setId(4);
        user.setNotificationTypes(new ArrayList<>());

        List<GameInShop> gameInShopList = Collections.emptyList();


        notificationManager.sendGameInfoChange(user, gameInShopList);


        verifyNoInteractions(emailNotificationService);
        verifyNoInteractions(smsNotificationService);
    }
}