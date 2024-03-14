package com.gpb.stores.configuration;

import com.gpb.stores.bean.game.ClientActivationType;
import com.gpb.stores.bean.game.Genre;
import com.gpb.stores.bean.game.ProductType;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EnumMapper {

    @Bean
    public Map<String, Genre> genereMap() {
        Map<String, Genre> genereMap = new HashMap<>();
        genereMap.put("Екшен", Genre.ACTION);
        genereMap.put("Пригоди", Genre.ADVENTURES);
        genereMap.put("Казуальні", Genre.CASUAL);
        genereMap.put("Гонки", Genre.RACE);
        genereMap.put("Рольові", Genre.RPG);
        genereMap.put("Інді", Genre.INDIE);
        genereMap.put("Онлайн", Genre.ONLINE);
        genereMap.put("Симулятори", Genre.SIMULATORS);
        genereMap.put("Стратегії", Genre.STRATEGIES);
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
