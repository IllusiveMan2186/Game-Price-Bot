package com.gpb.telegram.command.impl;

import com.gpb.common.service.UserLinkerService;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Command handler for retrieving a web connector token.
 */
@Component("token")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GetWebConnectorTokenCommandHandler implements CommandHandler {

    private final MessageSource messageSource;
    private final UserLinkerService userLinkerService;

    @Override
    public String getDescription(final Locale locale) {
        return messageSource.getMessage("accounts.synchronization.get.token.description", null, locale);
    }

    /**
     * Processes the command to get a web connector token.
     * <p>
     * It retrieves the token using the user's basic ID from the request by calling the
     * {@link UserLinkerService#getAccountsLinkerToken(long)} method and returns a {@link TelegramResponse}
     * containing the token.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing the necessary command data
     * @return a {@link TelegramResponse} with the web connector token as its message content
     */
    @Override
    public TelegramResponse apply(final TelegramRequest request) {
        final String token = userLinkerService.getAccountsLinkerToken(request.getUserBasicId());
        return new TelegramResponse(request, token);
    }
}