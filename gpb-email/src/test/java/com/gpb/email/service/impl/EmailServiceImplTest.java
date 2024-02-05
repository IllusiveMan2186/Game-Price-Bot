package com.gpb.email.service.impl;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    JavaMailSender mailSender ;
    @Mock
    TemplateEngine templateEngine ;

    @InjectMocks
    EmailServiceImpl emailService ;

    //TODO
    void sendEmailSuccessfullyShouldSendEmail() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        String templateName = "email-user-verification";
        Context context = new Context();
        //when(templateEngine.process(templateName, context)).thenReturn("");
        doNothing().when(templateEngine).process(templateName, context);

        emailService.sendEmail("email", "subject", context, templateName);

        verify(mailSender).send(mimeMessage);
    }
}