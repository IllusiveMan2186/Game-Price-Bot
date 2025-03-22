package com.gpb.game.unit.parser;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.configuration.mapper.GamazeyEnumMapper;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.parser.StorePageParser;
import com.gpb.game.parser.impl.GamazeyStoreParser;
import com.gpb.game.service.ResourceService;
import com.gpb.game.util.Constants;
import com.gpb.game.util.store.GamazeyConstants;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GamazeyStoreParserTest {

    @Mock
    private ResourceService resourceService;

    @Mock
    private StorePageParser storePageParser;

    @Mock
    private Map<String, ProductType> productTypeMap;

    @Mock
    private Map<String, ClientActivationType> clientActivationTypeMap;

    @Mock
    private Map<String, Genre> genreMap;

    private GamazeyStoreParser parser;
    private final GamazeyEnumMapper gamazeyEnumMapper = new GamazeyEnumMapper();

    @Mock
    private Document mockDocument;
    @Mock
    private Elements mockElements;
    @Mock
    private Element mockElement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        parser = new GamazeyStoreParser(
                resourceService,
                gamazeyEnumMapper.gamazeyGenreMap(),
                gamazeyEnumMapper.gamazeyProductTypeMap(),
                gamazeyEnumMapper.gamazeyClientActivationMap()
        );
    }

    @Test
    void shouldReturnGameInShop_whenParsingIsSuccessful_shouldReturnGameInShop() {
        setUpParseGamePage(
                "Доповнення Test Game - Some Dlc для ПК (Ключ активації Steam)",
                "6 299 ₴",
                "2 699 ₴",
                "-57%"
        );

        GameInShop result = parser.parseGameInShopFromPage(mockDocument);

        GameInShop expectedGameInShop = GameInShop.builder()
                .nameInStore("Test Game Some Dlc")
                .price(new BigDecimal("6299"))
                .discountPrice(new BigDecimal("2699"))
                .discount(57)
                .clientType(ClientActivationType.STEAM)
                .isAvailable(false)
                .build();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedGameInShop);
    }

    @Test
    void shouldReturnGameName_whenGetNameIsCalled_shouldReturnName() {
        Element nameElement = mock(Element.class);
        when(mockDocument.getElementsByClass(GamazeyConstants.GAME_PAGE_NAME_FIELD))
                .thenReturn(new Elements(nameElement));
        when(nameElement.text()).thenReturn("Game Name");

        String name = parser.getName(mockDocument);

        assertThat(name).isEqualTo("Game Name");
    }

    @Test
    void shouldReturnListOfUrls_whenSearchResultsParsedSuccessfully_shouldReturnUrlList() {
        Document searchPage = mock(Document.class);
        when(storePageParser.getPage(anyString())).thenReturn(Optional.of(searchPage));

        Element element1 = mock(Element.class);
        Element element2 = mock(Element.class);
        when(element1.child(0)).thenReturn(element1);
        when(element2.child(0)).thenReturn(element2);
        when(element1.attr(Constants.ATTRIBUTE_HREF)).thenReturn("/game1");
        when(element2.attr(Constants.ATTRIBUTE_HREF)).thenReturn("/game2");

        Elements elements = new Elements(List.of(element1, element2));

        when(searchPage.getElementsByClass(GamazeyConstants.GAME_IN_LIST)).thenReturn(elements);


        List<String> results = parser.parseSearchResults("Test", storePageParser);


        assertThat(results).containsExactly("/game1", "/game2");
    }

    @Test
    void shouldReturnGenres_whenGameHasGenres_shouldReturnGenres() {
        Element genreElement = mock(Element.class);
        when(genreElement.text()).thenReturn("Action, RPG");
        when(mockDocument.getElementsByClass(GamazeyConstants.GAME_PAGE_CHARACTERISTICS))
                .thenReturn(new Elements(genreElement));

        List<Genre> genres = parser.getGenres(mockDocument);

        assertThat(genres).isNotNull();
    }

    @Test
    void shouldReturnProductType_whenGamePageContainsProductType_shouldReturnGameProductType() {
        String name = "name";
        Element nameElement = mock(Element.class);
        when(mockDocument.getElementsByClass(GamazeyConstants.GAME_PAGE_NAME_FIELD))
                .thenReturn(new Elements(nameElement));
        when(nameElement.text()).thenReturn(name);

        ProductType type = parser.getProductType(mockDocument);

        assertThat(type).isEqualTo(ProductType.GAME);
    }

    @Test
    void testSaveImage_whenSuccess_shouldSaveImage() {
        when(mockDocument.getElementsByClass(GamazeyConstants.GAME_IMG_CLASS)).thenReturn(mockElements);
        when(mockElements.first()).thenReturn(mockElement);
        when(mockDocument.getElementsByClass(GamazeyConstants.GAME_PAGE_NAME_FIELD)).thenReturn(mockElements);
        when(mockElements.size()).thenReturn(5);
        when(mockElements.get(1)).thenReturn(mockElement);
        when(mockElement.attr("src")).thenReturn("http://image.url/game.jpg");
        when(mockElement.text()).thenReturn("Game Name");


        parser.saveImage(mockDocument);


        verify(resourceService, times(1)).saveCroppedImage(
                "http://image.url/game.jpg", "Game Name",
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_START, 0,
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_LONG, GamazeyConstants.GAME_IMAGE_HEIGHT);
    }

    @Test
    void testSaveImage_whenImageNotFound_shouldNotCallMethod() {
        when(mockDocument.getElementsByClass(GamazeyConstants.GAME_IMG_CLASS)).thenReturn(mockElements);
        when(mockElements.size()).thenReturn(0);
        when(mockDocument.getElementsByClass(GamazeyConstants.GAME_PAGE_NAME_FIELD)).thenReturn(mockElements);


        parser.saveImage(mockDocument);


        verify(resourceService, times(0)).saveCroppedImage(
                "http://image.url/game.jpg", "Game Name",
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_START, 0,
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_LONG, GamazeyConstants.GAME_IMAGE_HEIGHT);
    }

    @Test
    void testGetGenres_whenSuccess_shouldGetGenres() {
        Element genreElement = mock(Element.class);
        when(genreElement.text()).thenReturn("Жанр Екшен, Рольові");
        when(mockDocument.getElementsByClass(GamazeyConstants.GAME_PAGE_CHARACTERISTICS))
                .thenReturn(new Elements(genreElement));


        List<Genre> genres = parser.getGenres(mockDocument);


        assertEquals(2, genres.size());
        assertThat(genres).contains(Genre.ACTION, Genre.RPG);
    }

    private void setUpParseGamePage(String name, String price, String discountPrice, String discount) {
        setElementForClass(GamazeyConstants.GAME_PAGE_NAME_FIELD, name);
        setElementForClass(GamazeyConstants.GAME_PAGE_OLD_PRICE_FIELD, price);

        Element discountPriceElement = mock(Element.class);
        Element discountPriceChildElement = mock(Element.class);
        when(discountPriceElement.child(1)).thenReturn(discountPriceChildElement);
        when(discountPriceChildElement.text()).thenReturn(discountPrice);
        when(mockDocument.getElementsByClass(GamazeyConstants.GAME_PAGE_DISCOUNT_PRICE_FIELD))
                .thenReturn(new Elements(discountPriceElement));

        Element discountElement = mock(Element.class);
        when(discountElement.text()).thenReturn(discount);
        when(mockDocument.getElementById(GamazeyConstants.GAME_PAGE_DISCOUNT_FIELD))
                .thenReturn(discountElement);

        setElementForClass(GamazeyConstants.GAME_PAGE_IS_AVAILABLE, null);
    }

    private void setElementForClass(String className, String text) {
        Element element = mock(Element.class);
        when(mockDocument.getElementsByClass(className))
                .thenReturn(new Elements(element));
        if (text != null) when(element.text()).thenReturn(text);
    }
}
