package com.gpb.web.configuration;

import com.gpb.web.bean.game.ClientActivationType;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.bean.game.ProductType;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EnumMapper {

    @Bean
    public Map<String, Genre> genereMap() {
        Map<String, Genre> genereMap = new HashMap<>();
        genereMap.put("Экшен", Genre.ACTION);
        genereMap.put("Приключения", Genre.ADVENTURES);
        genereMap.put("Казуальные", Genre.CASUAL);
        genereMap.put("Гонки", Genre.RACE);
        genereMap.put("Ролевые", Genre.RPG);
        genereMap.put("Инди", Genre.INDIE);
        genereMap.put("Онлайн", Genre.ONLINE);
        genereMap.put("Симуляторы", Genre.SIMULATORS);
        genereMap.put("Стратегии", Genre.STRATEGIES);
        genereMap.put("Спорт", Genre.SPORT);
        return genereMap;
    }

    @Bean
    public Map<String, ClientActivationType> clientActtivationMap() {
        Map<String, ClientActivationType> clientActivationTypeMap = new HashMap<>();
        clientActivationTypeMap.put("EA App", ClientActivationType.EA);
        clientActivationTypeMap.put("Epic Games", ClientActivationType.EPIC);
        clientActivationTypeMap.put("Microsoft", ClientActivationType.MICROSOFT);
        clientActivationTypeMap.put("Steam", ClientActivationType.STEAM);
        clientActivationTypeMap.put("Ubisoft Connect", ClientActivationType.UBISOFT);
        clientActivationTypeMap.put("Rockstar", ClientActivationType.ROCKSTAR);
        clientActivationTypeMap.put("GOG", ClientActivationType.GOG);
        clientActivationTypeMap.put("Battle net", ClientActivationType.BATTLENET);
        return clientActivationTypeMap;
    }

    @Bean
    public Map<String, ProductType> productTypeMap() {
        Map<String, ProductType> productTypeMap = new HashMap<>();
        productTypeMap.put("Гра", ProductType.GAME);
        productTypeMap.put("Ігрова валюта", ProductType.CURRENCY);
        productTypeMap.put("місяців", ProductType.SUBSCRIPTION);
        productTypeMap.put("підписка", ProductType.SUBSCRIPTION);
        productTypeMap.put("Доповнення ", ProductType.ADDITION);
        return productTypeMap;
    }
}
