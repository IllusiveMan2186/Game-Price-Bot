package com.gpb.telegram.command.impl;

import com.gpb.common.util.CommonConstants;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.service.CommonRequestHandlerService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component("gameList")
@AllArgsConstructor
public class GameListCommandHandler implements CommandHandler {


    private final MessageSource messageSource;
    private final CommonRequestHandlerService commonRequestHandlerService;

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("game.list.description", null, locale);
    }

    @Override
    public TelegramResponse apply(TelegramRequest request) {
        int pageNum = 1;
        String sort = getSortParam(request);
        return commonRequestHandlerService.processGameListRequest(request, pageNum, sort);
    }

    private String getSortParam(TelegramRequest request) {
        String sortParam;
        try {
            sortParam = Constants.PRICE_SORT_PARAM_TELEGRAM_UI_FORMAT.equals(request.getArgument(1))
                    ? CommonConstants.PRICE_SORT_PARAM
                    : CommonConstants.NAME_SORT_PARAM;
        } catch (ArrayIndexOutOfBoundsException e) {
            sortParam = CommonConstants.NAME_SORT_PARAM;
        }

        String sortDirection;
        try {
            sortDirection = CommonConstants.SORT_DIRECTION_DESCENDING.toLowerCase().equals(request.getArgument(2))
                    ? CommonConstants.SORT_DIRECTION_DESCENDING
                    : CommonConstants.SORT_DIRECTION_ASCENDING;
        } catch (ArrayIndexOutOfBoundsException e) {
            sortDirection = CommonConstants.SORT_DIRECTION_ASCENDING;
        }

        return sortParam + "-" + sortDirection;
    }
}
