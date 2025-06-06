package com.gpb.game.unit.service.impl;

import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.resolver.NotificationServiceResolver;
import com.gpb.game.service.NotificationService;
import com.gpb.game.service.impl.NotificationManagerImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationManagerImplTest {

    @Mock
    private NotificationService emailNotificationService;
    @Mock
    private NotificationService telegramNotificationService;
    @Mock
    private ModelMapper mapper;
    @Mock
    private NotificationServiceResolver notificationFactory;

    @InjectMocks
    private NotificationManagerImpl notificationManager;

    @Test
    void testSendGameInfoChange_whenSingleNotificationType_shouldSendGameInfoChanges() {
        BasicUser user = new BasicUser();
        user.setId(1);
        user.setNotificationTypes(Collections.singletonList(UserNotificationType.EMAIL));

        GameInShop game1 = new GameInShop();
        GameInShop game2 = new GameInShop();
        List<GameInShop> gameInShopList = Arrays.asList(game1, game2);
        GameInStoreDto gameInStore1 = new GameInStoreDto();
        GameInStoreDto gameInStore2 = new GameInStoreDto();
        List<GameInStoreDto> gameInStoreDtoList = Arrays.asList(gameInStore1, gameInStore2);

        when(mapper.map(game1, GameInStoreDto.class)).thenReturn(gameInStore1);
        when(mapper.map(game2, GameInStoreDto.class)).thenReturn(gameInStore2);

        when(notificationFactory.getService(UserNotificationType.EMAIL)).thenReturn(emailNotificationService);


        notificationManager.sendGameInfoChange(user, gameInShopList);


        verify(emailNotificationService, times(1)).sendGameInfoChange(user, gameInStoreDtoList);
        verifyNoInteractions(telegramNotificationService);
    }

    @Test
    void testSendGameInfoChange_whenMultipleNotificationTypes_shouldSendGameInfoChanges() {
        BasicUser user = new BasicUser();
        user.setId(2);
        user.setNotificationTypes(Arrays.asList(UserNotificationType.EMAIL, UserNotificationType.TELEGRAM));

        GameInShop game1 = new GameInShop();
        List<GameInShop> gameInShopList = Collections.singletonList(game1);
        GameInStoreDto gameInStore1 = new GameInStoreDto();
        List<GameInStoreDto> gameInStoreDtoList = Collections.singletonList(gameInStore1);

        when(mapper.map(game1, GameInStoreDto.class)).thenReturn(gameInStore1);

        when(notificationFactory.getService(UserNotificationType.EMAIL)).thenReturn(emailNotificationService);
        when(notificationFactory.getService(UserNotificationType.TELEGRAM)).thenReturn(telegramNotificationService);


        notificationManager.sendGameInfoChange(user, gameInShopList);


        verify(emailNotificationService, times(1)).sendGameInfoChange(user, gameInStoreDtoList);
        verify(telegramNotificationService, times(1)).sendGameInfoChange(user, gameInStoreDtoList);
    }

    @Test
    void testSendGameInfoChange_whenNoNotificationTypes_shouldNotSendGameInfoChanges() {
        BasicUser user = new BasicUser();
        user.setId(3);
        user.setNotificationTypes(Collections.emptyList());

        List<GameInShop> gameInShopList = Collections.emptyList();


        notificationManager.sendGameInfoChange(user, gameInShopList);


        verifyNoInteractions(emailNotificationService);
        verifyNoInteractions(telegramNotificationService);
    }

    @Test
    void testSendGameInfoChange_whenNullNotificationType_shouldNotSendGameInfoChanges() {
        BasicUser user = new BasicUser();
        user.setId(4);
        user.setNotificationTypes(new ArrayList<>());

        List<GameInShop> gameInShopList = Collections.emptyList();


        notificationManager.sendGameInfoChange(user, gameInShopList);


        verifyNoInteractions(emailNotificationService);
        verifyNoInteractions(telegramNotificationService);
    }
}