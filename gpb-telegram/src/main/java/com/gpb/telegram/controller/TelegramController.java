package com.gpb.telegram.controller;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramController {

    SendMessage apply(String chatId, Update update);
}
