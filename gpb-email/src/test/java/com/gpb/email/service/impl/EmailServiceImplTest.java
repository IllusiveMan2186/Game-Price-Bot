package com.gpb.email.service.impl;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailServiceImpl(mailSender, templateEngine);
    }

    @Test
    void testSendEmail_Success_shouldSendMessage() throws Exception {
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
}
