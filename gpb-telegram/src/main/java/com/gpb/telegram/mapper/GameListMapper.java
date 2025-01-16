package com.gpb.telegram.mapper;

import com.gpb.common.entity.game.GameDto;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.util.ArrayList;
import java.util.List;


@Component
@AllArgsConstructor
public class GameListMapper {
    private final GameMapper gameMapper;
    private final ButtonFactory buttonFactory;


    public List<PartialBotApiMethod> gameSearchListToTelegramPage(List<GameDto> games,
                                                                  TelegramRequest request,
                                                                  long gameAmount,
                                                                  int pageNum,
                                                                  String gameName) {
        List<PartialBotApiMethod> messages = mapGamesToTelegramPage(games, request);
        if (isMorePagesExist(gameAmount, pageNum)) {
            messages.add(buttonFactory.getNextPageButtonForSearchByName(request.getChatId(), pageNum, gameName, request.getLocale()));
        }
        return messages;
    }

    public List<PartialBotApiMethod> userGameListToTelegramPage(List<GameDto> games,
                                                                TelegramRequest request,
                                                                long gameAmount,
                                                                int pageNum) {
        List<PartialBotApiMethod> messages = mapGamesToTelegramPage(games, request);
        if (isMorePagesExist(gameAmount, pageNum)) {
            messages.add(buttonFactory.getNextPageButtonForUserListOfGame(request.getChatId(), pageNum, request.getLocale()));
        }
        return messages;
    }

    public List<PartialBotApiMethod> gameListToTelegramPage(List<GameDto> games,
                                                            TelegramRequest request,
                                                            long gameAmount,
                                                            int pageNum,
                                                            String sort) {
        List<PartialBotApiMethod> messages = mapGamesToTelegramPage(games, request);
        if (isMorePagesExist(gameAmount, pageNum)) {
            messages.add(buttonFactory.getNextPageButtonForListOfGame(request.getChatId(), pageNum, request.getLocale(), sort));
        }
        return messages;
    }

    private List<PartialBotApiMethod> mapGamesToTelegramPage(List<GameDto> games, TelegramRequest request) {
        List<PartialBotApiMethod> messages = new ArrayList<>();
        games.forEach(game -> messages.add(gameMapper.getGamePhotoMessage(request, game)));
        return messages;
    }

    private boolean isMorePagesExist(long gameAmount, int pageNum) {
        long totalPages = (long) Math.ceil((double) gameAmount / Constants.GAMES_AMOUNT_IN_LIST);
        return pageNum < totalPages;
    }
}
