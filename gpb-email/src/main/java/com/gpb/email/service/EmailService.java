package com.gpb.email.service;

import org.thymeleaf.context.Context;

/**
 * Service interface for sending emails.
 */
public interface EmailService {

    /**
     * Sends an email to the specified recipient.
     *
     * @param to           the recipient's email address
     * @param subject      the subject of the email
     * @param context      the Thymeleaf context containing dynamic variables for the email template
     * @param templateName the name of the Thymeleaf email template to be used
     */
    void sendEmail(String to, String subject, Context context, String templateName);
}
