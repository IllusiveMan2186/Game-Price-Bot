package com.gpb.telegram.callback.impl;

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
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserGameListCallbackHandlerTest {
    @Mock
    CommonRequestHandlerService commonRequestHandlerService;
    @InjectMocks
    UserGameListCallbackHandler controller;

    @Test
    void testApply_whenSuccess_shouldReturnGameList() {
        int pageNum = 2;
        String chatId = "123456";
        TelegramResponse response = new TelegramResponse(new ArrayList<>());
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        Update update = UpdateCreator.getUpdateWithCallback("/userGameListCallback " + pageNum, Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(Locale.ENGLISH).user(user).build();
        when(commonRequestHandlerService.processGameListRequest(request, pageNum)).thenReturn(response);


        TelegramResponse result = controller.apply(request);


        assertEquals(result, response);
        verify(commonRequestHandlerService, times(1)).processGameListRequest(request, pageNum);
    }
}