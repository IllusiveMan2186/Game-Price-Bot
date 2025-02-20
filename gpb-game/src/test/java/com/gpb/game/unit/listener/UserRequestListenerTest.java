package com.gpb.game.unit.listener;

import com.gpb.common.entity.event.ChangeBasicUserIdEvent;
import com.gpb.common.entity.event.LinkUsersEvent;
import com.gpb.common.util.CommonConstants;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.listener.UserRequestListener;
import com.gpb.game.service.UserService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRequestListenerTest {

    @Mock
    private UserService userService;

    @Mock
    private KafkaTemplate<String, ChangeBasicUserIdEvent> changeIdEventKafkaTemplate;
    @InjectMocks
    private UserRequestListener userRequestListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListenGameFollow() {
        String token = "test-token";
        long currentUserBasicId = 123L;
        long newUserBasicId = 456L;

        LinkUsersEvent linkUsersEvent = new LinkUsersEvent();
        linkUsersEvent.setToken(token);
        linkUsersEvent.setCurrentUserBasicId(currentUserBasicId);

        BasicUser user = BasicUser.builder().id(newUserBasicId).build();

        ConsumerRecord<String, LinkUsersEvent> consumerRecord =
                new ConsumerRecord<>(CommonConstants.LINK_USERS_TOPIC, 0, 0, null, linkUsersEvent);

        when(userService.linkUsers(token, currentUserBasicId)).thenReturn(user);

        userRequestListener.listenLinkUsers(consumerRecord);


        verify(userService, times(1)).linkUsers(token, currentUserBasicId);
        ChangeBasicUserIdEvent event = new ChangeBasicUserIdEvent(
                currentUserBasicId,
                newUserBasicId);
        verify(changeIdEventKafkaTemplate, times(1))
                .send(CommonConstants.CHANGE_BASIC_USER_ID_TOPIC, "1", event);
    }
}