package com.gpb.game.unit.listener;

import com.gpb.game.bean.event.AccountLinkerEvent;
import com.gpb.game.listener.UserRequestListener;
import com.gpb.game.service.UserService;
import com.gpb.game.util.Constants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class UserRequestListenerTest {

    @Mock
    private UserService userService;

    private UserRequestListener userRequestListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRequestListener = new UserRequestListener(userService);
    }

    @Test
    void testListenUserSynchronizationAccounts() {
        String token = "token";
        long sourceUserId = 456L;

        AccountLinkerEvent event = new AccountLinkerEvent(token, sourceUserId);
        ConsumerRecord<String, AccountLinkerEvent> consumerRecord =
                new ConsumerRecord<>(Constants.USER_SYNCHRONIZATION_ACCOUNTS_TOPIC, 0, 0L, "key", event);


        userRequestListener.listenUserSynchronizationAccounts(consumerRecord);


        verify(userService, times(1)).linkUsers(token, sourceUserId);
    }
}
