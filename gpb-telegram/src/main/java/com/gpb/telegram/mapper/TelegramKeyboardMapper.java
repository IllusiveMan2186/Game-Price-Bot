package com.gpb.telegram.mapper;

import com.gpb.telegram.mapper.entity.TelegramButton;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@AllArgsConstructor
public class TelegramKeyboardMapper {

    private final MessageSource messageSource;

    public InlineKeyboardMarkup getKeyboardMarkup(List<List<TelegramButton>> linesSetting) {
        return new InlineKeyboardMarkup(getButtonLines(linesSetting));
    }

    private List<List<InlineKeyboardButton>> getButtonLines(List<List<TelegramButton>> linesSetting) {
        return linesSetting.stream().map(this::getButtonLine).toList();
    }

    private List<InlineKeyboardButton> getButtonLine(List<TelegramButton> telegramButtons) {
        return telegramButtons.stream().map(this::getButton).toList();
    }

    private InlineKeyboardButton getButton(TelegramButton telegramButton) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(messageSource.getMessage(telegramButton.getTextCode(), null, telegramButton.getLocale()));
        if (telegramButton.getCallBackData() != null) {
            inlineKeyboardButton.setCallbackData(telegramButton.getCallBackData());
        } else if (telegramButton.getUrl() != null) {
            inlineKeyboardButton.setUrl(telegramButton.getUrl());
        }
        return inlineKeyboardButton;
    }
}
