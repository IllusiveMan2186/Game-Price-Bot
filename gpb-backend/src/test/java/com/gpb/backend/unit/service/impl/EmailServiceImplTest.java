package com.gpb.backend.unit.service.impl;

import com.gpb.backend.bean.event.EmailEvent;
import com.gpb.backend.bean.event.EmailNotificationEvent;
import com.gpb.backend.bean.user.UserActivation;
import com.gpb.backend.bean.user.WebUser;
import com.gpb.backend.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static com.gpb.backend.util.Constants.EMAIL_SERVICE_TOPIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;

class EmailServiceImplTest {

    @Mock
    private KafkaTemplate<String, EmailEvent> kafkaTemplate;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendGameInfoChange_whenSuccess_shouldSendEmailEvent() {
        WebUser user = new WebUser();
        user.setEmail("test@example.com");
        user.setLocale(Locale.ENGLISH);

        EmailNotificationEvent notificationEvent = new EmailNotificationEvent();
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("key", "value");
        notificationEvent.setVariables(variables);


        emailService.sendGameInfoChange(user, notificationEvent);


        verify(kafkaTemplate).send(eq(EMAIL_SERVICE_TOPIC), any(String.class), any(EmailEvent.class));
    }

    @Test
    void testSendEmailVerification_whenSuccess_shouldSendVerificationEmailEvent() throws Exception {
        WebUser user = new WebUser();
        user.setEmail("user@example.com");
        user.setLocale(Locale.FRENCH);

        UserActivation userActivation = new UserActivation();
        userActivation.setToken("testToken");
        userActivation.setUser(user);

        Field webServiceUrlField = EmailServiceImpl.class.getDeclaredField("webServiceUrl");
        webServiceUrlField.setAccessible(true);
        webServiceUrlField.set(emailService, "http://localhost:8080");


        emailService.sendEmailVerification(userActivation);


        Map<String, Object> expectedVariables = new LinkedHashMap<>();
        expectedVariables.put("url", "http://localhost:8080/email/testToken");

        verify(kafkaTemplate).send(eq(EMAIL_SERVICE_TOPIC), any(String.class), argThat(emailEvent ->
                emailEvent.getRecipient().equals("user@example.com") &&
                        emailEvent.getSubject().equals("User verification") &&
                        emailEvent.getVariables().equals(expectedVariables) &&
                        emailEvent.getLocale().equals(Locale.FRENCH) &&
                        emailEvent.getTemplateName().equals("email-user-verification")
        ));
    }
}
