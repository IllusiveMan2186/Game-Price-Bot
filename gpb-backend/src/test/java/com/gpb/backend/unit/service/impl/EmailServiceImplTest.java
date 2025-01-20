package com.gpb.backend.unit.service.impl;

import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.service.impl.EmailServiceImpl;
import com.gpb.common.entity.event.EmailEvent;
import com.gpb.common.entity.event.NotificationEvent;
import com.gpb.common.util.CommonConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private KafkaTemplate<String, EmailEvent> kafkaTemplate;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void testSendGameInfoChange_whenSuccess_shouldSendEmailEvent() {
        WebUser user = new WebUser();
        user.setEmail("test@example.com");
        user.setLocale(Locale.ENGLISH);

        NotificationEvent notificationEvent = new NotificationEvent();


        emailService.sendGameInfoChange(user, notificationEvent);


        verify(kafkaTemplate).send(eq(CommonConstants.EMAIL_SERVICE_TOPIC), any(String.class), any(EmailEvent.class));
    }

    @Test
    void testSendEmailVerification_whenSuccess_shouldSendVerificationEmailEvent() throws Exception {
        WebUser user = new WebUser();
        user.setEmail("user@example.com");
        user.setLocale(Locale.FRENCH);

        UserActivation userActivation = new UserActivation();
        userActivation.setToken("testToken");
        userActivation.setUser(user);

        emailService.setFrontendServiceUrl("http://localhost:3000");


        emailService.sendEmailVerification(userActivation);


        Map<String, Object> expectedVariables = new LinkedHashMap<>();
        expectedVariables.put("url", "http://localhost:3000/activation?token=testToken");

        verify(kafkaTemplate).send(eq(CommonConstants.EMAIL_SERVICE_TOPIC), any(String.class), argThat(emailEvent ->
                emailEvent.getRecipient().equals("user@example.com") &&
                        emailEvent.getSubject().equals("User verification") &&
                        emailEvent.getVariables().equals(expectedVariables) &&
                        emailEvent.getLocale().equals(Locale.FRENCH) &&
                        emailEvent.getTemplateName().equals("email-user-verification")
        ));
    }
}
