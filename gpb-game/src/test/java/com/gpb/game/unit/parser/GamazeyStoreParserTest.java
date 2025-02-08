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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private GamazeyEnumMapper gamazeyEnumMapper = new GamazeyEnumMapper();

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
                gamazeyEnumMapper.gamazeyClientActivationMap());
    }

    @Test
    void testParseGameInShopFromPage_whenSuccess_shouldReturnGameInShop() {
        setUpParseGamePage(
                mockDocument,
                "Доповнення Test Game - Some Dlc для ПК (Ключ активації Steam)",
                "6 299 ₴",
                "2 699 ₴",
                "-57%");


        GameInShop result = parser.parseGameInShopFromPage(mockDocument);


        assertNotNull(result);
        GameInShop expectedGameInShop = GameInShop.builder()
                .nameInStore("Test Game - Some Dlc")
                .price(new BigDecimal("6299"))
                .discountPrice(new BigDecimal("2699"))
                .discount(57)
                .clientType(ClientActivationType.STEAM)
                .isAvailable(false)
                .build();
        assertEquals(expectedGameInShop, result);
    }

    @Test
    void testGetName_whenSuccess_shouldReturnName() {
        Element nameElement = mock(Element.class);
        when(mockDocument.getElementsByClass(GamazeyConstants.GAME_PAGE_NAME_FIELD))
                .thenReturn(new Elements(nameElement));
        when(nameElement.text()).thenReturn("Game Name");

        String name = parser.getName(mockDocument);


        assertEquals("Game Name", name);
    }

    @Test
    void testParseSearchResults_whenSuccess_shouldReturnListOfUrls() {
        Document searchPage = mock(Document.class);
        when(storePageParser.getPage(anyString())).thenReturn(searchPage);

        Element element1 = mock(Element.class);
        Element element2 = mock(Element.class);
        when(element1.child(0)).thenReturn(element1);
        when(element2.child(0)).thenReturn(element2);
        when(element1.attr(Constants.ATTRIBUTE_HREF)).thenReturn("/game1");
        when(element2.attr(Constants.ATTRIBUTE_HREF)).thenReturn("/game2");

        Elements elements = new Elements();
        elements.add(element1);
        elements.add(element2);

        when(searchPage.getElementsByClass(GamazeyConstants.GAME_IN_LIST)).thenReturn(elements);

        List<String> results = parser.parseSearchResults("Test", storePageParser);

        assertEquals(2, results.size());
        assertEquals("/game1", results.get(0));
        assertEquals("/game2", results.get(1));
    }

    @Test
    void testGetGenres_whenSuccess_shouldGetGenres() {
        Element genreElement = mock(Element.class);
        when(genreElement.text()).thenReturn("Action, RPG");
        when(mockDocument.getElementsByClass(GamazeyConstants.GAME_PAGE_CHARACTERISTICS))
                .thenReturn(new Elements(genreElement));

        List<Genre> genres = parser.getGenres(mockDocument);
        assertNotNull(genres);
    }

    @Test
    void testGetProductType_whenSuccess_shouldReturnProductType() {
        String name = "name";
        Element nameElement = mock(Element.class);
        when(mockDocument.getElementsByClass(GamazeyConstants.GAME_PAGE_NAME_FIELD))
                .thenReturn(new Elements(nameElement));
        when(nameElement.text()).thenReturn(name);


        ProductType type = parser.getProductType(mockDocument);


        assertEquals(ProductType.GAME, type);
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


        verify(resourceService, times(1)).cropImage(
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


        verify(resourceService, times(0)).cropImage(
                "http://image.url/game.jpg", "Game Name",
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_START, 0,
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_LONG, GamazeyConstants.GAME_IMAGE_HEIGHT);
    }

    void setUpParseGamePage(Document document,
                            String name,
                            String price,
                            String discountPrice,
                            String discount) {
        Element nameElement = mock(Element.class);
        Element priceElement = mock(Element.class);
        Element discountPriceElement = mock(Element.class);
        Element discountElement = mock(Element.class);
        Element availableElement = mock(Element.class);

        when(document.getElementsByClass(GamazeyConstants.GAME_PAGE_NAME_FIELD))
                .thenReturn(new Elements(nameElement));
        when(nameElement.text()).thenReturn(name);

        when(document.getElementsByClass(GamazeyConstants.GAME_PAGE_OLD_PRICE_FIELD))
                .thenReturn(new Elements(priceElement));
        when(priceElement.text()).thenReturn(price);

        when(document.getElementsByClass(GamazeyConstants.GAME_PAGE_DISCOUNT_PRICE_FIELD))
                .thenReturn(new Elements(discountPriceElement));
        when(discountPriceElement.text()).thenReturn(discountPrice);

        when(document.getElementById(GamazeyConstants.GAME_PAGE_DISCOUNT_FIELD))
                .thenReturn(discountElement);
        when(discountElement.text()).thenReturn(discount);

        when(document.getElementsByClass(GamazeyConstants.GAME_PAGE_IS_AVAILABLE))
                .thenReturn(new Elements(availableElement));


    }
}

