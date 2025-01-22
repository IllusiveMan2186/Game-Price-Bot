package com.gpb.telegram.command.impl;

import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Data
@Component("url")
public class GetWebUrlCommandHandler implements CommandHandler {

    @Value("${FRONT_SERVICE_URL}")
    private String frontendServiceUrl;

    private final MessageSource messageSource;

    public GetWebUrlCommandHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("get.web.url.description", null, locale);
    }

    @Override
    public TelegramResponse apply(TelegramRequest request) {
        return new TelegramResponse(request, frontendServiceUrl);
    }
}
