package com.gpb.web.configuration;

import com.gpb.web.bean.game.Genre;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GenreMapper {

    @Bean
    public Map<String, Genre> genereMap() {
        Map<String, Genre> genereMap = new HashMap<>();
        genereMap.put("Экшен",Genre.ACTION);
        genereMap.put("Приключения",Genre.ADVENTURES);
        genereMap.put("Казуальные",Genre.CASUAL);
        genereMap.put("Гонки",Genre.RACE);
        genereMap.put("Ролевые",Genre.RPG);
        genereMap.put("Инди",Genre.INDIE);
        genereMap.put("Онлайн",Genre.ONLINE);
        genereMap.put("Симуляторы",Genre.SIMULATORS);
        genereMap.put("Стратегии",Genre.STRATEGIES);
        genereMap.put("Спорт",Genre.SPORT);
        return genereMap;
    }
}
