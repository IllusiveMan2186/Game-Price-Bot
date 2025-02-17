package com.gpb.backend.unit.service.impl;

import com.gpb.backend.entity.EmailChanging;
import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.service.impl.EmailServiceImpl;
import com.gpb.common.entity.event.EmailEvent;
import com.gpb.common.entity.event.NotificationEvent;
import com.gpb.common.util.CommonConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private KafkaTemplate<String, EmailEvent> kafkaTemplate;
    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void testSendGameInfoChange_whenSuccess_shouldSendEmailEvent() {
        WebUser user = new WebUser();
        user.setEmail("test@example.com");
        user.setLocale(Locale.ENGLISH);

        when(messageSource.getMessage("email.subject.game.info.change", null, Locale.ENGLISH)).thenReturn("Game info changed");
        NotificationEvent notificationEvent = new NotificationEvent();


        emailService.sendGameInfoChange(user, notificationEvent);


        verify(kafkaTemplate).send(eq(CommonConstants.EMAIL_SERVICE_TOPIC), any(String.class), any(EmailEvent.class));
    }

    @Test
    void testSendEmailVerification_whenUserWithLocale_shouldSendVerificationEmailEventWithUserLocale() throws Exception {
        WebUser user = new WebUser();
        user.setEmail("user@example.com");
        user.setLocale(Locale.FRENCH);

        UserActivation userActivation = new UserActivation();
        userActivation.setToken("testToken");
        userActivation.setUser(user);

        emailService.setFrontendServiceUrl("http://localhost:3000");
        when(messageSource.getMessage("email.subject.user.verification", null, Locale.FRENCH)).thenReturn("User verification");


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

    @Test
    void testSendEmailVerification_whenUserWithoutLocale_shouldSendVerificationEmailEventWithDefaultLocale() throws Exception {
        WebUser user = new WebUser();
        user.setEmail("user@example.com");

        UserActivation userActivation = new UserActivation();
        userActivation.setToken("testToken");
        userActivation.setUser(user);

        emailService.setFrontendServiceUrl("http://localhost:3000");
        when(messageSource.getMessage("email.subject.user.verification", null, Locale.getDefault())).thenReturn("User verification");


        emailService.sendEmailVerification(userActivation);


        Map<String, Object> expectedVariables = new LinkedHashMap<>();
        expectedVariables.put("url", "http://localhost:3000/activation?token=testToken");

        verify(kafkaTemplate).send(eq(CommonConstants.EMAIL_SERVICE_TOPIC), any(String.class), argThat(emailEvent ->
                emailEvent.getRecipient().equals("user@example.com") &&
                        emailEvent.getSubject().equals("User verification") &&
                        emailEvent.getVariables().equals(expectedVariables) &&
                        emailEvent.getLocale().equals(Locale.getDefault()) &&
                        emailEvent.getTemplateName().equals("email-user-verification")
        ));
    }


    @Test
    void testSendEmailChanging_whenSuccess_shouldSendEmailsToNewAndOldEmails() throws Exception {
        EmailChanging emailChanging = EmailChanging.builder()
                .id(12L)
                .newEmail("email@mail.com")
                .oldEmailToken("oldToken")
                .newEmailToken("newToken")
                .build();
        WebUser user = WebUser.builder()
                .email("user@example.com")
                .locale(new Locale("en"))
                .build();
        emailChanging.setUser(user);
        when(messageSource.getMessage("email.subject.email.change", null, user.getLocale())).thenReturn("Email change");
        emailService.setFrontendServiceUrl("http://localhost:3000");


        emailService.sendEmailChange(emailChanging);


        Map<String, Object> expectedNewEmailVariables = new LinkedHashMap<>();
        expectedNewEmailVariables.put("url", "http://localhost:3000/email/change/confirm?token=newToken");

        Map<String, Object> expectedOldEmailVariables = new LinkedHashMap<>();
        expectedOldEmailVariables.put("url", "http://localhost:3000/email/change/confirm?token=oldToken");

        InOrder inOrder = inOrder(kafkaTemplate);

        inOrder.verify(kafkaTemplate).send(eq(CommonConstants.EMAIL_SERVICE_TOPIC), any(String.class), argThat(emailEvent ->
                emailEvent.getRecipient().equals("email@mail.com") &&
                        emailEvent.getSubject().equals("Email change") &&
                        emailEvent.getVariables().equals(expectedNewEmailVariables) &&
                        emailEvent.getLocale().equals(user.getLocale()) &&
                        emailEvent.getTemplateName().equals("email-changing-email")
        ));

        inOrder.verify(kafkaTemplate).send(eq(CommonConstants.EMAIL_SERVICE_TOPIC), any(String.class), argThat(emailEvent ->
                emailEvent.getRecipient().equals("user@example.com") &&
                        emailEvent.getSubject().equals("Email change") &&
                        emailEvent.getVariables().equals(expectedOldEmailVariables) &&
                        emailEvent.getLocale().equals(user.getLocale()) &&
                        emailEvent.getTemplateName().equals("email-changing-email")
        ));
    }
}
