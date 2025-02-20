package com.gpb.telegram.mapper;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.telegram.entity.TelegramRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.util.ArrayList;
import java.util.List;


@Component
@AllArgsConstructor
public class GameInfoMapper {

    private final GameMapper gameMapper;
    private final GameInStoreMapper gameInStoreMapper;


    public List<PartialBotApiMethod> mapGameInfoToTelegramPage(GameInfoDto game, TelegramRequest request) {
        List<PartialBotApiMethod> messages = new ArrayList<>();
        messages.add(gameMapper.mapGameToPhotoMessage(request, game));
        messages.addAll(game.getGamesInShop()
                .stream()
                .map(gameInShop -> gameInStoreMapper.mapGamesInStoreToTelegramPage(request.getChatId(), gameInShop, request.getLocale()))
                .toList());
        return messages;
    }
}
