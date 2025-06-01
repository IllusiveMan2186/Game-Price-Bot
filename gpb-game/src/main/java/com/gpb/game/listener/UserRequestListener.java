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

/**
 * Kafka listener for processing user linking events.
 * <p>
 * This listener handles events where user accounts are linked using a provided token.
 * Upon receiving a {@link LinkUsersEvent}, it invokes the user service to link the users and then publishes a
 * {@link ChangeBasicUserIdEvent} to notify other systems about the change in the basic user ID.
 * </p>
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserRequestListener {


    private final UserService userService;
    private final KafkaTemplate<String, ChangeBasicUserIdEvent> changeIdEventKafkaTemplate;

    /**
     * Listens for user linking events on the configured Kafka topic and processes the event.
     * <p>
     * When a {@link LinkUsersEvent} is received, this method links the user accounts by calling the user service.
     * It then publishes a {@link ChangeBasicUserIdEvent} with the old and new user IDs to the appropriate Kafka topic.
     * </p>
     *
     * @param linkRecord the Kafka consumer record containing a {@link LinkUsersEvent} with user linking information.
     */
    @KafkaListener(
            topics = CommonConstants.LINK_USERS_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "linkUsersListener"
    )
    @Transactional
    public void listenLinkUsers(final ConsumerRecord<String, LinkUsersEvent> linkRecord) {
        final LinkUsersEvent linkUsersEvent = linkRecord.value();
        log.info("Received request to link user with current basic ID '{}' using token '{}'",
                linkUsersEvent.getCurrentUserBasicId(), linkUsersEvent.getToken());

        final BasicUser newUserVersion = userService.linkUsers(
                linkUsersEvent.getToken(),
                linkUsersEvent.getCurrentUserBasicId()
        );

        log.info("Publishing change basic user ID event: old ID '{}', new ID '{}'",
                linkUsersEvent.getCurrentUserBasicId(), newUserVersion.getId());

        final ChangeBasicUserIdEvent changeEvent = new ChangeBasicUserIdEvent(
                linkUsersEvent.getCurrentUserBasicId(),
                newUserVersion.getId()
        );

        changeIdEventKafkaTemplate.send(CommonConstants.CHANGE_BASIC_USER_ID_TOPIC, "1", changeEvent);
    }
}
