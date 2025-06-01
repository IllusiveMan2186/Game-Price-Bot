package com.gpb.game.unit.resolver.store;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.configuration.mapper.CardmagEnumMapper;
import com.gpb.game.resolver.store.CardmagTypesResolver;
import com.gpb.game.util.store.CardmagConstants;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CardmagTypesResolverTest {

    private CardmagTypesResolver resolver;
    private Document mockDocument;

    private final CardmagEnumMapper cardmagEnumMapper = new CardmagEnumMapper();

    @BeforeEach
    void setUp() {
        Map<String, Genre> genreMap = cardmagEnumMapper.cardmagGenreMap();
        Map<String, ProductType> productTypeMap = cardmagEnumMapper.cardmagProductTypeMap();
        Map<String, ClientActivationType> clientActivationTypeMap = cardmagEnumMapper.cardmagClientActivationMap();

        resolver = new CardmagTypesResolver(genreMap, productTypeMap, clientActivationTypeMap);
        mockDocument = mock(Document.class);
    }

    @Test
    void testResolveGenres_whenMultipleGenres_shouldReturnAllMatchingGenres() {
        String title = "КАТАЛОГ ПРОДУКЦІЇ Sports Strategy Action +Big Buck Hunter Arcade Xbox Live Key EUROPE";


        List<Genre> result = resolver.resolveGenres(title);


        assertThat(result).containsExactlyInAnyOrder(Genre.SPORT, Genre.STRATEGIES, Genre.ACTION, Genre.ARCADE);
    }

    @Test
    void testResolveGenres_whenNoGenres_shouldReturnEmptyGenres() {
        String title = "КАТАЛОГ ПРОДУКЦІЇ Цифрові ключі активації Steam (Стім) Warhammer: Vermintide 2";


        List<Genre> result = resolver.resolveGenres(title);


        assertThat(result).isEmpty();
    }

    @Test
    void testResolveProductType_whenTitleNotContainTypeButDlcElement_shouldReturnAdditionProductType() {
        String title = "КАТАЛОГ ПРОДУКЦІЇ Xbox Live Key - UNITED STATES";

        Element dummyElement = mock(Element.class);
        Elements elements = new Elements(dummyElement);
        when(mockDocument.getElementsByClass(CardmagConstants.DLC_FIELD)).thenReturn(elements);


        ProductType result = resolver.resolveProductType(title, mockDocument);


        assertThat(result).isEqualTo(ProductType.ADDITION);
    }

    @Test
    void testGetProductType_whenTitleNotContainType_shouldReturnGameProductType() {
        String title = "Unknown game";

        Element dummyElement = mock(Element.class);
        Elements elements = new Elements(dummyElement);
        when(mockDocument.getElementsByClass(CardmagConstants.DLC_FIELD)).thenReturn(elements);


        ProductType result = resolver.resolveProductType(title, mockDocument);


        assertThat(result).isEqualTo(ProductType.ADDITION);
    }

    @Test
    void testGetProductType_whenTitleContainDlcField_shouldReturnAdditionProductType() {
        String title = "КАТАЛОГ ПРОДУКЦІЇ Gaming DLCs Test Game - Some Dlc (Xbox One) - Xbox Live Key - UNITED STATES";

        when(mockDocument.getElementsByClass(CardmagConstants.DLC_FIELD)).thenReturn(new Elements());


        ProductType result = resolver.resolveProductType(title, mockDocument);


        assertThat(result).isEqualTo(ProductType.ADDITION);
    }

    @Test
    void testResolveActivationType_whenGameFromXbox_shouldReturnMicrosoftActivationType() {
        String title = "КАТАЛОГ ПРОДУКЦІЇ Gaming DLCs Test Game - Some Dlc (Xbox One) - Xbox Live Key - UNITED STATES";


        ClientActivationType result = resolver.resolveActivationType(title);


        assertThat(result).isEqualTo(ClientActivationType.MICROSOFT);
    }

    @Test
    void testResolveActivationType_whenGameFromUnknown_shouldReturnNullActivationType() {
        String title = "Some unknown platform";


        ClientActivationType result = resolver.resolveActivationType(title);


        assertThat(result).isNull();
    }
}