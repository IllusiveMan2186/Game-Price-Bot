package com.gpb.telegram.mapper.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Locale;

@Data
@Builder
public class TelegramButton {
    private String textCode;
    private String callBackData;
    private String url;
    private Locale locale;
}
