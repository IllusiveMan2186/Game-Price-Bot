package com.gpb.email.listener;

import com.gpb.email.bean.EmailEvent;
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

    @KafkaListener(topics = "gpb-email", groupId = "email-event")
    public void emailEventListen(ConsumerRecord<Long, EmailEvent> eventRecord) {
        EmailEvent emailEvent = eventRecord.value();
        log.info(String.format("Email event '%s' for recipient '%s' about '%s'", eventRecord.key(),
                emailEvent.getRecipient(), emailEvent.getSubject()));

        Context context = new Context();
        context.setLocale(emailEvent.getLocale());
        context.setVariables(emailEvent.getVariables());

        emailService.sendEmail(emailEvent.getRecipient(), emailEvent.getSubject(), context,
                emailEvent.getTemplateName());
    }
}
