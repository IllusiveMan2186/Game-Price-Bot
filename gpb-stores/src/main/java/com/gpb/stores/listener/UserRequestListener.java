package com.gpb.stores.listener;

import com.gpb.stores.bean.event.AccountLinkerEvent;
import com.gpb.stores.service.UserService;
import com.gpb.stores.util.Constants;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class UserRequestListener {

    private final UserService userService;

    public UserRequestListener(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics = Constants.USER_SYNCHRONIZATION_ACCOUNTS_TOPIC, groupId = Constants.GPB_KAFKA_GROUP_ID)
    @Transactional
    public void listenUserSynchronizationAccounts(ConsumerRecord<String, AccountLinkerEvent> unfollowRecord) {
        AccountLinkerEvent event = unfollowRecord.value();
        log.info("Request for merging {} with user {}", event.getToken(), event.getSourceUserId());
        userService.linkUsers(event.getToken(), event.getSourceUserId());
    }
}
