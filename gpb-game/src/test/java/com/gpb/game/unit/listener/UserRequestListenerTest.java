package com.gpb.game.unit.listener;

import com.gpb.game.bean.event.AccountLinkerEvent;
import com.gpb.game.listener.UserRequestListener;
import com.gpb.game.service.UserService;
import com.gpb.game.util.Constants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserRequestListenerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRequestListener userRequestListener;


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
