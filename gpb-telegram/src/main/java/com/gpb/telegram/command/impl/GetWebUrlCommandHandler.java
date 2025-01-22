package com.gpb.telegram.command.impl;

import org.springframework.beans.factory.annotation.Value;

public class GetWebUrlCommandHandler implements CommandHandler {


    @Value("${KAFKA_SERVER_URL}")
    private String kafkaServer;

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
