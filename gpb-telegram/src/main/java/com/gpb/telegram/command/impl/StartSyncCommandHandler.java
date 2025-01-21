package com.gpb.telegram.command.impl;

import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class StartSyncCommandHandler implements CommandHandler {

    private SynchronizeToWebUserCommandHandler synchronizeToWebUserCommandHandler;

    @Override
    public String getDescription(Locale locale) {
        return synchronizeToWebUserCommandHandler.getDescription(locale);
    }

    @Override
    @Transactional
    public TelegramResponse apply(TelegramRequest request) {
        return synchronizeToWebUserCommandHandler.apply(request);
    }
}
