package com.gpb.telegram.controller.impl;

import com.gpb.telegram.controller.TelegramController;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Consts;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component("getSynchronizeToken")
@FilterChainMarker(Consts.USER_EXISTING_FILTER)
public class GetWebConnectorTokenController implements TelegramController {

    private final TelegramUserService telegramUserService;

    public GetWebConnectorTokenController(TelegramUserService telegramUserService) {
        this.telegramUserService = telegramUserService;
    }

    @Override
    public String getDescription() {
        return " - give you token for synchronization on web part";
    }

    @Override
    public SendMessage apply(String chatId, Update update) {
        long userId = update.getMessage().getFrom().getId();

        String token = telegramUserService.getWebUserConnectorToken(userId);

        return new SendMessage(chatId, token);
    }
}
