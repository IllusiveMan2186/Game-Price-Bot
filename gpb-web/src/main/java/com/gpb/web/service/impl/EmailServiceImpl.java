package com.gpb.web.service.impl;

import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.UserActivation;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Value("${WEB_SERVICE_URL}")
    private String webServiceUrl;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendGameInfoChange(WebUser user, List<GameInShop> gameInShopList) {
        Context context = new Context();
        context.setVariable("games", gameInShopList);
        context.setLocale(user.getLocale());
        sendEmail(user.getEmail(), "Game info changes", context, "email-info-changed-template");
    }

    @Override
    public void sendEmailVerification(UserActivation userActivation) {
        Context context = new Context();
        context.setVariable("url", webServiceUrl + "/email/" + userActivation.getToken());
        context.setLocale(userActivation.getUser().getLocale());
        sendEmail(userActivation.getUser().getEmail(), "User verification", context, "email-user-verification");
    }

    private void sendEmail(String to, String subject, Context context, String templateName) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("Send changed info about subscribed game for user " + to);
        } catch (MessagingException e) {
            log.error("Error during email sending : " + e.getMessage());
        } catch (MailAuthenticationException e) {
            log.error("GPB mail credential error : " + e.getMessage());
        }
    }
}
