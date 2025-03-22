package com.gpb.email.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private TemplateEngine templateEngine;
    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void testSendEmail_whenSuccess_shouldSendMessage() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate";
        Context context = new Context();
        context.setVariable("name", "John Doe");

        String htmlContent = "<p>Message!</p>";
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(templateName), any(Context.class))).thenReturn(htmlContent);

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);


        emailService.sendEmail(to, subject, context, templateName);


        verify(templateEngine, times(1)).process(eq(templateName), eq(context));
        verify(mailSender, times(1)).send(mimeMessageCaptor.capture());

        MimeMessage capturedMimeMessage = mimeMessageCaptor.getValue();
        assertNotNull(capturedMimeMessage);
    }

    @Test
    void testSendEmail_whenWrongAuthenticationCredential_shouldWriteToLog() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate";
        Context context = new Context();
        context.setVariable("name", "John Doe");

        String htmlContent = "<p>Message!</p>";
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(templateName), any(Context.class))).thenReturn(htmlContent);

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        doThrow(new MailAuthenticationException("")).when(mailSender).send(mimeMessage);


            emailService.sendEmail(to, subject, context, templateName);


        verify(templateEngine, times(1)).process(eq(templateName), eq(context));
        verify(mailSender, times(1)).send(mimeMessageCaptor.capture());
    }
}
