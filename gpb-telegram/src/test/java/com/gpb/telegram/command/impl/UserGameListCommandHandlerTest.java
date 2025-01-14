package com.gpb.telegram.command.impl;

import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.service.CommonRequestHandlerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserGameListCommandHandlerTest {
    @Mock
    MessageSource messageSource;
    @Mock
    CommonRequestHandlerService commonRequestHandlerService;
    @InjectMocks
    UserGameListCommandHandler controller;

    @Test
    void testGetDescription_whenSuccess_shouldReturnDescription() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("game.user.game.list.command.description", null, locale))
                .thenReturn("messages");


        String description = controller.getDescription(locale);


        assertEquals("messages", description);
    }

    @Test
    void testApply_whenSuccess_shouldReturnGameList() {
        int pageNum = 1;
        TelegramResponse response = new TelegramResponse(new ArrayList<>());
        TelegramRequest request = TelegramRequest.builder().build();
        when(commonRequestHandlerService.processGameListRequest(request, pageNum)).thenReturn(response);


        TelegramResponse result = controller.apply(request);


        assertEquals(result, response);
        verify(commonRequestHandlerService, times(1)).processGameListRequest(request, pageNum);
    }
}