package com.gpb.email.service;

import org.thymeleaf.context.Context;

public interface EmailService {

    /**
     * Send email to user
     *
     * @param to recipient
     * @param subject subject of email
     * @param context context of email
     * @param templateName template name of mail
     */
    void sendEmail(String to, String subject, Context context, String templateName);
}
