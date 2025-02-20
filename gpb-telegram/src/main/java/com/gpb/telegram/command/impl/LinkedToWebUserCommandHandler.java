package com.gpb.telegram.command.impl;

import com.gpb.common.exception.RestTemplateRequestException;
import com.gpb.common.service.UserLinkerService;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Command handler for linking a Telegram user account to a web user account.
 */
@Slf4j
@Component(Constants.SYNCHRONIZATION_COMMAND)
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class LinkedToWebUserCommandHandler implements CommandHandler {

    private final MessageSource messageSource;
    private final UserLinkerService userLinkerService;
    private final TelegramUserService telegramUserService;

    @Override
    public String getDescription(final Locale locale) {
        return messageSource.getMessage("accounts.synchronization.description", null, locale);
    }

    /**
     * Processes the synchronization command.
     * <p>
     * This method extracts a token from the Telegram request arguments and attempts to link the
     * user's Telegram account with a web account via {@link UserLinkerService#linkAccounts(String, long)}.
     * If successful, it returns a TelegramResponse containing a confirmation message. If an error occurs
     * it returns a response with an appropriate error message.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing the command details and user information
     * @return a {@link TelegramResponse} with the result of the synchronization operation
     */
    @Override
    @Transactional
    public TelegramResponse apply(final TelegramRequest request) {
        final String token = request.getArgument(1);
        try {
            userLinkerService.linkAccounts(token, request.getUserBasicId());
            String successMessage = messageSource.getMessage(
                    "accounts.synchronization.token.connected.message",
                    null,
                    request.getLocale());
            return new TelegramResponse(request, successMessage);
        } catch (RestTemplateRequestException exception) {
            log.error("Error linking accounts for user basic id {}: {}",
                    request.getUserBasicId(), exception.getMessage(), exception);
            String errorMessage = messageSource.getMessage(
                    "accounts.linked.already",
                    null,
                    request.getLocale());
            return new TelegramResponse(request, errorMessage);
        }
    }
}
