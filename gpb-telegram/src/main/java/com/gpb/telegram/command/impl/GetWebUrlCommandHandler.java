package com.gpb.telegram.command.impl;

import com.gpb.common.service.UserLinkerService;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.util.Constants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Data
@Component("url")
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GetWebUrlCommandHandler implements CommandHandler {

    @Value("${FRONT_SERVICE_URL}")
    private String frontendServiceUrl;
    private final MessageSource messageSource;
    private final UserLinkerService userLinkerService;

    public GetWebUrlCommandHandler(MessageSource messageSource, UserLinkerService userLinkerService) {
        this.messageSource = messageSource;
        this.userLinkerService = userLinkerService;
    }

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("get.web.url.description", null, locale);
    }

    @Override
    public TelegramResponse apply(TelegramRequest request) {
        String url = frontendServiceUrl + Constants.SET_LINK_TOKEN +
                userLinkerService.getAccountsLinkerToken(request.getUserBasicId());
        return new TelegramResponse(request, url);
    }
}
