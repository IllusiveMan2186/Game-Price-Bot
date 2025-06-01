package com.gpb.game.unit.resolver.store;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.configuration.mapper.GamazeyEnumMapper;
import com.gpb.game.resolver.store.GamazeyTypesResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GamazeyTypesResolverTest {

    private GamazeyTypesResolver resolver;

    private final GamazeyEnumMapper gamazeyEnumMapper = new GamazeyEnumMapper();

    @BeforeEach
    void setUp() {
        Map<String, Genre> genreMap = gamazeyEnumMapper.gamazeyGenreMap();
        Map<String, ProductType> productTypeMap = gamazeyEnumMapper.gamazeyProductTypeMap();
        Map<String, ClientActivationType> clientActivationTypeMap = gamazeyEnumMapper.gamazeyClientActivationMap();

        resolver = new GamazeyTypesResolver(genreMap, productTypeMap, clientActivationTypeMap);
    }

    @Test
    void testResolveProductType_whenSpecifyProductType_shouldReturnType() {
        ProductType type = resolver.resolveProductType("Доповнення Sid Meier's Civilization VI - Gathering Storm");

        assertThat(type).isEqualTo(ProductType.ADDITION);
    }

    @Test
    void testResolveProductType_whenNoSpecifyProductType_shouldReturnGame() {
        ProductType type = resolver.resolveProductType("Minecraft");

        assertThat(type).isEqualTo(ProductType.GAME);
    }

    @Test
    void testResolveActivationType_whenActivationTypeSpecified_shouldReturnType() {
        ClientActivationType type = resolver.resolveActivationType("Game для ПК (Ключ активації Steam)");

        assertThat(type).isEqualTo(ClientActivationType.STEAM);
    }

    @Test
    void testResolveActivationType_whenNoSpecifyActivationType_shouldReturnNull() {
        ClientActivationType type = resolver.resolveActivationType("Game");

        assertThat(type).isNull();
    }

    @Test
    void testResolveGenres_whenMultipleGenres_shouldReturnsGenres() {
        List<Genre> genres = resolver.resolveGenres("Екшен Рольові");

        assertThat(genres).containsExactlyInAnyOrder(Genre.ACTION, Genre.RPG);
    }

    @Test
    void testResolveGenres_whenNoGenres_shouldReturnsEmptyList() {
        List<Genre> genres = resolver.resolveGenres("");

        assertThat(genres).isEmpty();
    }
}
