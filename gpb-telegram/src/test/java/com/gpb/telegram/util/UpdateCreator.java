package com.gpb.telegram.util;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class UpdateCreator {

    public static Update getUpdateWithoutCallback(String text, long chatId) {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        long userId = 123456;

        chat.setId(chatId);
        update.setMessage(message);
        message.setText(text);
        message.setChat(chat);
        User user = new User();

        update.setMessage(message);
        message.setFrom(user);
        user.setId(userId);
        user.setLanguageCode("");
        return update;
    }

    public static Update getUpdateWithCallback(String text, long chatId) {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        Chat chat = new Chat();
        long userId = 123456;

        chat.setId(chatId);
        update.setCallbackQuery(callbackQuery);
        callbackQuery.setData(text);
        message.setChat(chat);
        callbackQuery.setMessage(message);
        User user = new User();

        update.setMessage(message);
        callbackQuery.setFrom(user);
        user.setId(userId);
        user.setLanguageCode("");
        return update;
    }
}
