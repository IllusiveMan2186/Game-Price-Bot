package com.gpb.telegram.command.impl;

import com.gpb.common.util.CommonConstants;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.service.CommonRequestHandlerService;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameListCommandHandlerTest {

    @Mock
    MessageSource messageSource;
    @Mock
    CommonRequestHandlerService commonRequestHandlerService;
    @InjectMocks
    GameListCommandHandler controller;

    @Test
    void testGetDescription_whenSuccess_shouldReturnDescription() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("game.list.description", null, locale))
                .thenReturn("messages");
        String description = controller.getDescription(locale);

        assertEquals("messages", description);
    }

    @Test
    void testApply_whenSortParamNameAndSortDirection_shouldReturnGameList() {
        String sortParam = "price";
        String sortDirection = "desc";
        String chatId = "123456";
        int pageNum = 1;
        String sortExpected = CommonConstants.PRICE_SORT_PARAM + "-" + CommonConstants.SORT_DIRECTION_DESCENDING;
        TelegramResponse response = new TelegramResponse(new ArrayList<>());
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        String textParam = "/gameList " + sortParam + " " + sortDirection;
        Update update = UpdateCreator.getUpdateWithCallback(textParam, Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(Locale.ENGLISH).user(user).build();
        when(commonRequestHandlerService.processGameListRequest(request, pageNum, sortExpected)).thenReturn(response);


        TelegramResponse result = controller.apply(request);


        assertEquals(result, response);
        verify(commonRequestHandlerService, times(1)).processGameListRequest(request, pageNum, sortExpected);
    }

    @Test
    void testApply_whenHaveSortParamButNoSortDirection_shouldReturnGameListWithDefaultValueOfSortDirection() {
        String sortParam = "price";
        String chatId = "123456";
        int pageNum = 1;
        String sortExpected = CommonConstants.PRICE_SORT_PARAM + "-" + CommonConstants.SORT_DIRECTION_ASCENDING;
        TelegramResponse response = new TelegramResponse(new ArrayList<>());
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        String textParam = "/gameList " + sortParam;
        Update update = UpdateCreator.getUpdateWithCallback(textParam, Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(Locale.ENGLISH).user(user).build();
        when(commonRequestHandlerService.processGameListRequest(request, pageNum, sortExpected)).thenReturn(response);


        TelegramResponse result = controller.apply(request);


        assertEquals(result, response);
        verify(commonRequestHandlerService, times(1)).processGameListRequest(request, pageNum, sortExpected);
    }

    @Test
    void testApply_whenWrongSortParam_shouldReturnGameListWithDefaultValueOfSortParam() {
        String sortParam = "wrongSortParam";
        String sortDirection = "desc";
        String chatId = "123456";
        int pageNum = 1;
        String sortExpected = CommonConstants.NAME_SORT_PARAM + "-" + CommonConstants.SORT_DIRECTION_DESCENDING;
        TelegramResponse response = new TelegramResponse(new ArrayList<>());
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        String textParam = "/gameList " + sortParam + " " + sortDirection;
        Update update = UpdateCreator.getUpdateWithCallback(textParam, Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(Locale.ENGLISH).user(user).build();
        when(commonRequestHandlerService.processGameListRequest(request, pageNum, sortExpected)).thenReturn(response);


        TelegramResponse result = controller.apply(request);


        assertEquals(result, response);
        verify(commonRequestHandlerService, times(1)).processGameListRequest(request, pageNum, sortExpected);
    }

    @Test
    void testApply_whenWrongSortDirection_shouldReturnGameListWithDefaultValueOfSortDirection() {
        String sortParam = "name";
        String sortDirection = "wrongSortDirection";
        String chatId = "123456";
        int pageNum = 1;
        String sortExpected = CommonConstants.NAME_SORT_PARAM + "-" + CommonConstants.SORT_DIRECTION_ASCENDING;
        TelegramResponse response = new TelegramResponse(new ArrayList<>());
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        String textParam = "/gameList " + sortParam + " " + sortDirection;
        Update update = UpdateCreator.getUpdateWithCallback(textParam, Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(Locale.ENGLISH).user(user).build();
        when(commonRequestHandlerService.processGameListRequest(request, pageNum, sortExpected)).thenReturn(response);


        TelegramResponse result = controller.apply(request);


        assertEquals(result, response);
        verify(commonRequestHandlerService, times(1)).processGameListRequest(request, pageNum, sortExpected);
    }

    @Test
    void testApply_whenNoSortParams_shouldReturnGameListWithDefaultValueOfSorting() {
        String chatId = "123456";
        int pageNum = 1;
        String sortExpected = CommonConstants.NAME_SORT_PARAM + "-" + CommonConstants.SORT_DIRECTION_ASCENDING;
        TelegramResponse response = new TelegramResponse(new ArrayList<>());
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        Update update = UpdateCreator.getUpdateWithCallback("/gameList ", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(Locale.ENGLISH).user(user).build();
        when(commonRequestHandlerService.processGameListRequest(request, pageNum, sortExpected)).thenReturn(response);


        TelegramResponse result = controller.apply(request);


        assertEquals(result, response);
        verify(commonRequestHandlerService, times(1)).processGameListRequest(request, pageNum, sortExpected);
    }
}