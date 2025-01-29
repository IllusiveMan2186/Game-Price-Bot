package com.gpb.common.listener;

import com.gpb.common.entity.event.ChangeBasicUserIdEvent;
import com.gpb.common.service.ChangeUserBasicIdService;
import com.gpb.common.util.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service interface for handling events related to basic user ID changes.
 * <p>
 * To use this service in your application, you must:
 * <ul>
 *   <li>Implement the {@code ChangeUserBasicIdService} interface to define the specific behavior for handling events.</li>
 *   <li>Specify 'spring.kafka.consumer.group-id' in application properties.</li>
 *   <li>Initialize a {@code KafkaTemplate<String, LinkUsersEvent>} using the {@code KafkaProducerFactory.createLinkUsersEventEventTemplate()} method.</li>
 * </ul>
 */
@Slf4j
@Component
@AllArgsConstructor
public class ChangeBasicUserIdListener {

    private final ChangeUserBasicIdService changeUserBasicIdService;

    /**
     * Listen change basic user id event
     *
     * @param changeIdRecord Change basic user id event record
     */
    @KafkaListener(topics = CommonConstants.CHANGE_BASIC_USER_ID_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "changeIdListener")
    @Transactional
    public void listenChangeId(ConsumerRecord<String, ChangeBasicUserIdEvent> changeIdRecord) {
        ChangeBasicUserIdEvent event = changeIdRecord.value();
        log.info("Request for change basic user id {} to {}", event.getOldBasicUserId(), event.getNewBasicUserId());
        changeUserBasicIdService.setBasicUserId(event.getOldBasicUserId(), event.getNewBasicUserId());
    }
}
