package com.gpb.game.unit.parser;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.configuration.mapper.CardmagEnumMapper;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.parser.StorePageParser;
import com.gpb.game.parser.impl.CardmagStoreParser;
import com.gpb.game.service.ResourceService;
import com.gpb.game.util.Constants;
import com.gpb.game.util.store.CardmagConstants;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CardmagStoreParserTest {
    @Mock
    private ResourceService resourceService;
    @Mock
    private Document mockDocument;
    @Mock
    private Element mockElement;
    @Mock
    private Elements mockElements;
    @Mock
    private StorePageParser pageFetcher;

    private CardmagStoreParser parser;
    private CardmagEnumMapper cardmagEnumMapper = new CardmagEnumMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        parser = new CardmagStoreParser(
                resourceService,
                cardmagEnumMapper.cardmagGenreMap(),
                cardmagEnumMapper.cardmagProductTypeMap(),
                cardmagEnumMapper.cardmagClientActivationMap());
    }

    @Test
    void testParseGameInShopFromPage_whenSuccess_shouldReturnGameInShop() {
        setUpParseGamePage(
                mockDocument,
                "Test Game - Some DLC (Xbox One) - Xbox Live Key - UNITED STATES",
                "1 200 ₴",
                "КАТАЛОГ ПРОДУКЦІЇ Gaming DLCs Test Game - Some Dlc (Xbox One) - Xbox Live Key - UNITED STATES"
        );


        GameInShop result = parser.parseGameInShopFromPage(mockDocument);


        assertNotNull(result);
        GameInShop expectedGameInShop = GameInShop.builder()
                .nameInStore("Test Game - Some DLC")
                .price(new BigDecimal("1200"))
                .discountPrice(new BigDecimal("1200"))
                .discount(0)
                .isAvailable(true)
                .clientType(ClientActivationType.MICROSOFT)
                .build();
        assertEquals(expectedGameInShop, result);
    }

    @Test
    void testGetName_whenSuccess_shouldReturnName() {
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_NAME_FIELD)).thenReturn(mockElements);
        when(mockElements.first()).thenReturn(mockElement);
        when(mockElement.text()).thenReturn("Game Name");


        String name = parser.getName(mockDocument);


        assertEquals("Game Name", name);
    }

    @Test
    void testParseSearchResults_whenSuccess_shouldReturnListOfUrls() {
        when(pageFetcher.getPage(anyString())).thenReturn(mockDocument);
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_IN_LIST)).thenReturn(mockElements);
        when(mockElements.stream()).thenReturn(List.of(mockElement).stream());
        when(mockElement.attr(Constants.ATTRIBUTE_HREF)).thenReturn("/game-link");


        List<String> results = parser.parseSearchResults("Some Game", pageFetcher);


        assertEquals(1, results.size());
        assertEquals("/game-link", results.get(0));
    }

    @Test
    void testGetGenres_whenSuccess_shouldGetGenres() {
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_TITLE_FIELD)).thenReturn(mockElements);
        when(mockElements.get(0)).thenReturn(mockElement);
        when(mockElement.text()).thenReturn("Action RPG");


        List<Genre> genres = parser.getGenres(mockDocument);


        assertNotNull(genres);
    }

    @Test
    void testGetProductType_whenSuccess_shouldReturnProductType() {
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_TITLE_FIELD)).thenReturn(mockElements);
        when(mockElements.first()).thenReturn(mockElement);
        when(mockElement.text()).thenReturn("КАТАЛОГ ПРОДУКЦІЇ Gaming DLCs Test Game - Some Dlc (Xbox One) - Xbox Live Key - UNITED STATES");


        ProductType productType = parser.getProductType(mockDocument);


        assertEquals(ProductType.ADDITION, productType);
    }

    @Test
    void testSaveImage_whenSuccess_shouldSaveImage() {
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_IMG_CLASS)).thenReturn(mockElements);
        when(mockElements.isEmpty()).thenReturn(false);
        when(mockElements.first()).thenReturn(mockElement);
        when(mockElement.attr("src")).thenReturn("http://image.url/game.jpg");
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_NAME_FIELD)).thenReturn(mockElements);
        when(mockElements.first()).thenReturn(mockElement);
        when(mockElement.text()).thenReturn("Game Name");


        parser.saveImage(mockDocument);


        verify(resourceService, times(1)).cropImage(anyString(), anyString(), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void testSaveImage_whenImageNotFound_shouldNotCallMethod() {
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_IMG_CLASS)).thenReturn(new Elements());

        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_NAME_FIELD)).thenReturn(mockElements);
        when(mockElements.first()).thenReturn(mockElement);
        when(mockElement.text()).thenReturn("Game Name");


        parser.saveImage(mockDocument);


        verify(resourceService, times(0)).cropImage(anyString(), anyString(), anyInt(), anyInt(), anyInt(), anyInt());
    }

    void setUpParseGamePage(Document document, String name, String price, String title) {
        Element nameElement = mock(Element.class);
        Element priceElement = mock(Element.class);
        Element availableElement = mock(Element.class);
        Element titleElement = mock(Element.class);

        when(document.getElementsByClass(CardmagConstants.GAME_PAGE_NAME_FIELD))
                .thenReturn(new Elements(nameElement));
        when(nameElement.text()).thenReturn(name);

        when(document.select(CardmagConstants.GAME_PRICE_FIELD))
                .thenReturn(new Elements(priceElement));
        when(priceElement.text()).thenReturn(price);

        when(document.getElementsByClass(CardmagConstants.GAME_PAGE_IS_AVAILABLE))
                .thenReturn(new Elements(availableElement));


        when(document.getElementsByClass(CardmagConstants.GAME_PAGE_TITLE_FIELD))
                .thenReturn(new Elements(titleElement));
        when(titleElement.text()).thenReturn(title);

        Element imageElement = mock(Element.class);
        when(document.getElementsByClass(CardmagConstants.GAME_IMG_CLASS))
                .thenReturn(new Elements(imageElement));
        when(imageElement.attr("src")).thenReturn("imgUrl");

    }
}
