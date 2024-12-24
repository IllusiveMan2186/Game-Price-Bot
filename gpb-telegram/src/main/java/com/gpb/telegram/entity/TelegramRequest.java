package com.gpb.telegram.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Locale;

@Data
@Builder
@AllArgsConstructor
public class TelegramRequest {

    private Update update;
    private Locale locale;
    private TelegramUser user;

    public TelegramRequest(Update update) {
        this.update = update;
    }

    public String getArgument(int index) {
        String messageText = update.hasCallbackQuery()
                ? update.getCallbackQuery().getData()
                : update.getMessage().getText();
        return messageText.split(" ")[index];
    }

    public int getIntArgument(int index) {
        String messageText = update.hasCallbackQuery()
                ? update.getCallbackQuery().getData()
                : update.getMessage().getText();
        return Integer.parseInt(messageText.split(" ")[index]);
    }

    public long getLongArgument(int index) {
        String messageText = update.hasCallbackQuery()
                ? update.getCallbackQuery().getData()
                : update.getMessage().getText();
        return Long.parseLong(messageText.split(" ")[index]);
    }

    public long getUserId() {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom().getId()
                : update.getMessage().getFrom().getId();
    }

    public String getChatId() {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getMessage().getChatId().toString()
                : update.getMessage().getChatId().toString();
    }

    public String getCommandName() {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getData().split(" ")[0].replace("/", "")
                : update.getMessage().getText().split(" ")[0].replace("/", "");
    }

    public User getFrom(){
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom()
                : update.getMessage().getFrom();
    }

    public long getUserBasicId(){
        return user.getBasicUserId();
    }
}
