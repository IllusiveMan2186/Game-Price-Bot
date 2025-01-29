package com.gpb.game.listener;

import com.gpb.common.entity.event.ChangeBasicUserIdEvent;
import com.gpb.common.entity.event.LinkUsersEvent;
import com.gpb.common.util.CommonConstants;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class UserRequestListener {

    private final UserService userService;
    private final KafkaTemplate<String, ChangeBasicUserIdEvent> changeIdEventKafkaTemplate;

    @KafkaListener(topics = CommonConstants.LINK_USERS_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "linkUsersListener")
    @Transactional
    public void listenLinkUsers(ConsumerRecord<String, LinkUsersEvent> linkRecord) {
        LinkUsersEvent linkUsersEvent = linkRecord.value();
        log.info("Request link user {} by token {}",
                linkUsersEvent.getCurrentUserBasicId(), linkUsersEvent.getToken());

        BasicUser newUserVersion = userService.linkUsers(
                linkUsersEvent.getToken(),
                linkUsersEvent.getCurrentUserBasicId());

        log.info("Send change basic user id event for old id {} and new id {}",
                linkUsersEvent.getCurrentUserBasicId(),
                newUserVersion.getId());

        changeIdEventKafkaTemplate.send(CommonConstants.CHANGE_BASIC_USER_ID_TOPIC, "1",
                new ChangeBasicUserIdEvent(
                        linkUsersEvent.getCurrentUserBasicId(),
                        newUserVersion.getId()));
    }
}
