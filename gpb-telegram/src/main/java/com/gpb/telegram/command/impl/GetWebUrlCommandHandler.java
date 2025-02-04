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

/**
 * Command handler for generating a web URL that incorporates the user's link token.
 */
@Data
@Component("url")
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GetWebUrlCommandHandler implements CommandHandler {

    @Value("${FRONT_SERVICE_URL}")
    private String frontendServiceUrl;

    private final MessageSource messageSource;
    private final UserLinkerService userLinkerService;

    public GetWebUrlCommandHandler(final MessageSource messageSource, final UserLinkerService userLinkerService) {
        this.messageSource = messageSource;
        this.userLinkerService = userLinkerService;
    }

    @Override
    public String getDescription(final Locale locale) {
        return messageSource.getMessage("get.web.url.description", null, locale);
    }

    /**
     * Processes the command to generate a web URL that includes the user's link token.
     * <p>
     * The URL is built by concatenating the frontend service URL with a specific link token path and the token
     * obtained from the {@link UserLinkerService}.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing the user's basic ID
     * @return a {@link TelegramResponse} containing the generated URL
     */
    @Override
    public TelegramResponse apply(final TelegramRequest request) {
        final String token = userLinkerService.getAccountsLinkerToken(request.getUserBasicId());
        final String url = frontendServiceUrl + Constants.SET_LINK_TOKEN + token;
        return new TelegramResponse(request, url);
    }
}
