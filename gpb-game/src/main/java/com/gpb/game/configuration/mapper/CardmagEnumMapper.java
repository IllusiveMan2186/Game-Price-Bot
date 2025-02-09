package com.gpb.game.configuration.mapper;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration class for mapping values from the Cardmag store to their corresponding enumeration constants.
 * <p>
 * This configuration provides bean definitions for converting localized string representations used by the Cardmag store
 * into their corresponding {@link Genre}, {@link ClientActivationType}, and {@link ProductType} enums.
 * </p>
 */
@Configuration
public class CardmagEnumMapper {

    /**
     * Provides a mapping between localized genre names from Cardmag and their corresponding {@link Genre} enums.
     *
     * @return an immutable {@link Map} where the keys are localized genre names
     * and the values are the corresponding {@link Genre} enums.
     */
    @Bean(name = "cardmagGeners")
    public Map<String, Genre> cardmagGenreMap() {
        return Map.ofEntries(
                Map.entry("Action", Genre.ACTION),
                Map.entry("Adventure", Genre.ADVENTURES),
                Map.entry("Casual", Genre.CASUAL),
                Map.entry("Racing", Genre.RACE),
                Map.entry("RPG", Genre.RPG),
                Map.entry("Indie", Genre.INDIE),
                Map.entry("Simulator", Genre.SIMULATORS),
                Map.entry("Strategy", Genre.STRATEGIES),
                Map.entry("Sports", Genre.SPORT),
                Map.entry("Puzzle", Genre.PUZZLE),
                Map.entry("Horror", Genre.HORROR),
                Map.entry("Arcade", Genre.ARCADE),
                Map.entry("Stealth", Genre.STEALTH)
        );
    }

    /**
     * Provides a mapping between client activation type names from Cardmag and their corresponding
     * {@link ClientActivationType} enums.
     *
     * @return an immutable {@link Map} where the keys are client activation type names and the values are the corresponding
     * {@link ClientActivationType} enums.
     */
    @Bean(name = "cardmagClientActivation")
    public Map<String, ClientActivationType> cardmagClientActivationMap() {
        return Map.ofEntries(
                Map.entry("EA App", ClientActivationType.EA),
                Map.entry("Epic Games", ClientActivationType.EPIC),
                Map.entry("Windows", ClientActivationType.MICROSOFT),
                Map.entry("Xbox", ClientActivationType.MICROSOFT),
                Map.entry("Steam", ClientActivationType.STEAM),
                Map.entry("Ubisoft", ClientActivationType.UBISOFT),
                Map.entry("Rockstar", ClientActivationType.ROCKSTAR),
                Map.entry("GOG", ClientActivationType.GOG),
                Map.entry("Battle.net", ClientActivationType.BATTLENET)
        );
    }

    /**
     * Provides a mapping between localized product type names from Cardmag and their corresponding {@link ProductType} enums.
     *
     * @return an immutable {@link Map} where the keys are localized product type names
     * and the values are the corresponding {@link ProductType} enums.
     */
    @Bean(name = "cardmagProductTypes")
    public Map<String, ProductType> cardmagProductTypeMap() {
        return Map.ofEntries(
                Map.entry("Points & Currencies", ProductType.CURRENCY),
                Map.entry("Gaming Subscriptions", ProductType.SUBSCRIPTION),
                Map.entry("DLC", ProductType.ADDITION)
        );
    }
}
