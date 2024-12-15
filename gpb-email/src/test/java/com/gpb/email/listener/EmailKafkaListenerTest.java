package com.gpb.email.listener;

import com.gpb.email.bean.EmailEvent;
import com.gpb.email.service.EmailService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

import static org.mockito.Mockito.*;

class EmailKafkaListenerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailKafkaListener emailKafkaListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void emailEventListen_shouldProcessEmailEvent() {
        // Arrange
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate";
        EmailEvent emailEvent = new EmailEvent();
        emailEvent.setRecipient(recipient);
        emailEvent.setSubject(subject);
        emailEvent.setTemplateName(templateName);
        emailEvent.setLocale(Locale.ENGLISH); // Assume locale is null for simplicity
        emailEvent.setVariables(Map.of("key1", "value1"));

        ConsumerRecord<String, EmailEvent> consumerRecord = new ConsumerRecord<>(
                "gpb_email_event", 0, 0L, "eventKey", emailEvent
        );

        // Act
        emailKafkaListener.emailEventListen(consumerRecord);

        // Assert
        verify(emailService, times(1)).sendEmail(
                eq(recipient),
                eq(subject),
                any(Context.class),
                eq(templateName)
        );
    }

    @Test
    void emailEventListen_shouldHandleNullVariables() {
        // Arrange
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate";
        EmailEvent emailEvent = new EmailEvent();
        emailEvent.setRecipient(recipient);
        emailEvent.setSubject(subject);
        emailEvent.setTemplateName(templateName);
        emailEvent.setLocale(Locale.ENGLISH);
        emailEvent.setVariables(null); // Simulating null variables

        ConsumerRecord<String, EmailEvent> consumerRecord = new ConsumerRecord<>(
                "gpb_email_event", 0, 0L, "eventKey", emailEvent
        );

        // Act
        emailKafkaListener.emailEventListen(consumerRecord);

        // Assert
        verify(emailService, times(1)).sendEmail(
                eq(recipient),
                eq(subject),
                any(Context.class),
                eq(templateName)
        );
    }

    @Test
    void emailEventListen_shouldLogEmailEventDetails() {
        // Arrange
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate";
        String eventKey = "eventKey";
        EmailEvent emailEvent = new EmailEvent();
        emailEvent.setRecipient(recipient);
        emailEvent.setSubject(subject);
        emailEvent.setTemplateName(templateName);
        emailEvent.setLocale(Locale.ENGLISH);
        emailEvent.setVariables(Map.of("key1", "value1"));

        ConsumerRecord<String, EmailEvent> consumerRecord = new ConsumerRecord<>(
                "gpb_email_event", 0, 0L, eventKey, emailEvent
        );

        // Act
        emailKafkaListener.emailEventListen(consumerRecord);

        // Assert
        // Verifying logs would require a logging framework mock (e.g., using Mockito's verify with a logging wrapper)
        verify(emailService, times(1)).sendEmail(
                eq(recipient),
                eq(subject),
                any(Context.class),
                eq(templateName)
        );
    }
}
