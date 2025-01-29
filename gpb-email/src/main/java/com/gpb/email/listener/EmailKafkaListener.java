package com.gpb.email.listener;

import com.gpb.common.entity.event.EmailEvent;
import com.gpb.common.util.CommonConstants;
import com.gpb.email.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Slf4j
@Component
public class EmailKafkaListener {

    private final EmailService emailService;

    public EmailKafkaListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = CommonConstants.EMAIL_SERVICE_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "emailEventListener")
    public void emailEventListen(ConsumerRecord<String, EmailEvent> eventRecord) {
        EmailEvent emailEvent = eventRecord.value();
        log.info("Email event '{}' for recipient '{}' about '{}'", eventRecord.key(),
                emailEvent.getRecipient(), emailEvent.getSubject());

        Context context = new Context();
        context.setLocale(emailEvent.getLocale());
        context.setVariables(emailEvent.getVariables());

        emailService.sendEmail(emailEvent.getRecipient(), emailEvent.getSubject(), context,
                emailEvent.getTemplateName());
    }
}
