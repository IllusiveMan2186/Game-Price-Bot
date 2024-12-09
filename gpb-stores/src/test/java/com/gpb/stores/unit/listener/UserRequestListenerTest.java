package com.gpb.stores.unit.listener;

import com.gpb.stores.bean.event.AccountLinkerEvent;
import com.gpb.stores.listener.UserRequestListener;
import com.gpb.stores.service.UserService;
import com.gpb.stores.util.Constants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

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
