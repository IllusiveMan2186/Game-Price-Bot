package com.gpb.web.listener;

import com.gpb.web.bean.game.Game;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import com.gpb.web.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class GameRequestListener {

    @Autowired
    private GameStoresService gameStoresService;
    @Autowired
    private GameService gameService;
    //private KafkaTemplate<String, List<Long>> responseKafkaTemplate;

    @KafkaListener(topics = Constants.GAME_NAME_SEARCH_TOPIC, groupId = Constants.GPB_KAFKA_GROUP_ID,
            containerFactory = Constants.RESPONSE_CONTAINER_FACTORY)
    @SendTo//(Constants.GAME_SEARCH_RESPONSE_TOPIC)
    public List<Long> listenGameNameSearch(ConsumerRecord<String, String> record,@Header(KafkaHeaders.CORRELATION_ID) String correlationId) {
        log.info(String.format("Request '%s' for searching of game with name '%s'", record.key(), record.value()));
        //record.headers().headers(KafkaHeaders.CORRELATION_ID)
        //String correlationId2 = new String(record.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value());
        //System.out.println(Arrays.toString(record.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value()));
        List<Game> games = gameStoresService.findGameByName(record.value());
        List<Long> gamesId = gameService.addGames(games);

        System.out.println(gamesId);
        return gamesId;
    }

    @KafkaListener(topics = Constants.GAME_URL_SEARCH_TOPIC, groupId = Constants.GPB_KAFKA_GROUP_ID,
            containerFactory = Constants.RESPONSE_CONTAINER_FACTORY)
    @SendTo(Constants.GAME_SEARCH_RESPONSE_TOPIC)
    public List<Long>  listenGameUrlSearch(ConsumerRecord<String, String> record) {
        log.info(String.format("Request '%s' for searching of game with url '%s'", record.key(), record.value()));
        Game game = gameStoresService.findGameByUrl(record.value());
        return gameService.addGames(Collections.singletonList(game));
        //responseKafkaTemplate.send(Constants.GAME_SEARCH_RESPONSE_TOPIC, record.key(), gamesId);
    }

    //@KafkaListener(topics = "gpb_game_follow", groupId = "gpb")
    public void listenGameFollow(ConsumerRecord<String, Long> record) {
        log.info(String.format("Request '%s' for follow for game '%s'", record.key(), record.value()));
        Game game = gameService.getById(record.value());
        gameStoresService.subscribeToGame(game);
    }


    //@KafkaListener(topics = "gpb_game_unfollow", groupId = "gpb")
    public void listenGameUnfollow(ConsumerRecord<String, Long> record) {
        log.info(String.format("Request '%s' for unfollow for game '%s'", record.key(), record.value()));
        Game game = gameService.getById(record.value());
        gameStoresService.unsubscribeFromGame(game);
    }
}
