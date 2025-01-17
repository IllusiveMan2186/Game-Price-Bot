package com.gpb.backend.unit.listener;

import com.gpb.backend.entity.WebUser;
import com.gpb.backend.listener.EmailNotificationListener;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.UserManagementService;
import com.gpb.common.entity.event.NotificationEvent;
import com.gpb.common.util.CommonConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailNotificationListenerTest {

    @Mock
    private UserManagementService userService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailNotificationListener emailNotificationListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListenEmailNotification_whenSuccess_shouldProcessEvent() {
        long basicUserId = 123L;
        NotificationEvent event = new NotificationEvent();
        event.setBasicUserId(basicUserId);

        ConsumerRecord<String, NotificationEvent> record = new ConsumerRecord<>(CommonConstants.EMAIL_NOTIFICATION_TOPIC, 0, 0, "key", event);

        WebUser user = new WebUser();
        when(userService.getUserByBasicUserId(basicUserId)).thenReturn(user);


        emailNotificationListener.listenEmailNotification(record);


        verify(userService).getUserByBasicUserId(basicUserId);
        verify(emailService).sendGameInfoChange(user, event);
    }
}
