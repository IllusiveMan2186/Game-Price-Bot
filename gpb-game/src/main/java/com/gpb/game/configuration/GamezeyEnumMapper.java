package com.gpb.game.configuration;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for mapping values from the Gamezey store to their corresponding enumeration constants.
 * <p>
 * This configuration provides bean definitions for converting localized string representations used by the Gamezey store
 * into their corresponding {@link Genre}, {@link ClientActivationType}, and {@link ProductType} enums.
 * </p>
 */
@Configuration
public class GamezeyEnumMapper {

    /**
     * Provides a mapping between localized genre names from Gamezey and their corresponding {@link Genre} enums.
     *
     * @return a {@link Map} where the keys are localized genre names
     * and the values are the corresponding {@link Genre} enums.
     */
    @Bean
    public Map<String, Genre> gamezeyGenreMap() {
        Map<String, Genre> genreMap = new HashMap<>();
        genreMap.put("Екшен", Genre.ACTION);
        genreMap.put("Пригоди", Genre.ADVENTURES);
        genreMap.put("Казуальні", Genre.CASUAL);
        genreMap.put("Гонки", Genre.RACE);
        genreMap.put("Рольові", Genre.RPG);
        genreMap.put("Інді", Genre.INDIE);
        genreMap.put("Онлайн", Genre.ONLINE);
        genreMap.put("Симулятори", Genre.SIMULATORS);
        genreMap.put("Стратегії", Genre.STRATEGIES);
        genreMap.put("Спорт", Genre.SPORT);
        return genreMap;
    }

    /**
     * Provides a mapping between client activation type names from Gamezey and their corresponding
     * {@link ClientActivationType} enums.
     *
     * @return a {@link Map} where the keys are client activation type names and the values are the corresponding
     * {@link ClientActivationType} enums.
     */
    @Bean
    public Map<String, ClientActivationType> gamezeyClientActivationMap() {
        Map<String, ClientActivationType> clientActivationMap = new HashMap<>();
        clientActivationMap.put("EA App", ClientActivationType.EA);
        clientActivationMap.put("Epic Games", ClientActivationType.EPIC);
        clientActivationMap.put("Microsoft", ClientActivationType.MICROSOFT);
        clientActivationMap.put("Steam", ClientActivationType.STEAM);
        clientActivationMap.put("Ubisoft Connect", ClientActivationType.UBISOFT);
        clientActivationMap.put("Rockstar", ClientActivationType.ROCKSTAR);
        clientActivationMap.put("GOG", ClientActivationType.GOG);
        clientActivationMap.put("Battle net", ClientActivationType.BATTLENET);
        return clientActivationMap;
    }

    /**
     * Provides a mapping between localized product type names from Gamezey and their corresponding {@link ProductType} enums.
     *
     * @return a {@link Map} where the keys are localized product type names
     * and the values are the corresponding {@link ProductType} enums.
     */
    @Bean
    public Map<String, ProductType> gamezeyProductTypeMap() {
        Map<String, ProductType> productTypeMap = new HashMap<>();
        productTypeMap.put("Гра", ProductType.GAME);
        productTypeMap.put("Ігрова валюта", ProductType.CURRENCY);
        productTypeMap.put("місяців", ProductType.SUBSCRIPTION);
        productTypeMap.put("підписка", ProductType.SUBSCRIPTION);
        productTypeMap.put("Доповнення", ProductType.ADDITION); // Removed trailing whitespace
        return productTypeMap;
    }
}
