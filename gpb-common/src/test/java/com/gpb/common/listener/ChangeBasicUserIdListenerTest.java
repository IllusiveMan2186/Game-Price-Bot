package com.gpb.common.listener;

import com.gpb.common.entity.event.ChangeBasicUserIdEvent;
import com.gpb.common.service.ChangeUserBasicIdService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChangeBasicUserIdListenerTest {

    @Mock
    private ChangeUserBasicIdService changeUserBasicIdService;

    @InjectMocks
    private ChangeBasicUserIdListener changeBasicUserIdListener;

    @Test
    void testListenChangeId_whenSuccess_shouldCallServiceChangeUserBasicIdService() {
        long oldBasicUserId = 123;
        long newBasicUserId = 456;

        ChangeBasicUserIdEvent event = new ChangeBasicUserIdEvent();
        event.setOldBasicUserId(oldBasicUserId);
        event.setNewBasicUserId(newBasicUserId);

        ConsumerRecord<String, ChangeBasicUserIdEvent> consumerRecord = new ConsumerRecord<>(
                "topic",
                0,
                0,
                null,
                event
        );


        changeBasicUserIdListener.listenChangeId(consumerRecord);


        verify(changeUserBasicIdService, times(1)).setBasicUserId(eq(oldBasicUserId), eq(newBasicUserId));
    }
}
