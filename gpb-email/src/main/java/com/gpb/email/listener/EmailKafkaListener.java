package com.gpb.email.listener;

import com.gpb.common.entity.event.EmailEvent;
import com.gpb.common.util.CommonConstants;
import com.gpb.email.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

/**
 * Listens for email events on the Kafka topic defined in {@link CommonConstants#EMAIL_SERVICE_TOPIC}
 * and delegates the processing of these events to the {@link EmailService}.
 */
@Slf4j
@Component
public class EmailKafkaListener {

    private final EmailService emailService;

    public EmailKafkaListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Listens to email events from Kafka, logs the event details, prepares the email context,
     * and sends an email based on the event data.
     *
     * @param eventRecord the consumer record containing the email event details
     */
    @KafkaListener(
            topics = CommonConstants.EMAIL_SERVICE_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "emailEventListener"
    )
    public void emailEventListen(ConsumerRecord<String, EmailEvent> eventRecord) {
        if (eventRecord == null) {
            log.warn("Received null ConsumerRecord, skipping processing.");
            return;
        }

        EmailEvent emailEvent = eventRecord.value();
        if (emailEvent == null) {
            log.warn("Received null EmailEvent for key: {}", eventRecord.key());
            return;
        }

        log.info("Received email event with key: {} for recipient: {} and subject: {}",
                eventRecord.key(), emailEvent.getRecipient(), emailEvent.getSubject());

        try {
            Context context = new Context();
            context.setLocale(emailEvent.getLocale());
            context.setVariables(emailEvent.getVariables());

            emailService.sendEmail(
                    emailEvent.getRecipient(),
                    emailEvent.getSubject(),
                    context,
                    emailEvent.getTemplateName()
            );

            log.info("Email sent successfully to: {}", emailEvent.getRecipient());
        } catch (Exception ex) {
            log.error("Error processing email event for recipient: {}", emailEvent.getRecipient(), ex);
        }
    }
}
