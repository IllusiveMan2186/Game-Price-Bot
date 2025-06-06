package com.gpb.game.unit.parser;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.parser.StorePageParser;
import com.gpb.game.parser.impl.CardmagStoreParser;
import com.gpb.game.resolver.store.CardmagTypesResolver;
import com.gpb.game.service.ResourceService;
import com.gpb.game.util.Constants;
import com.gpb.game.util.store.CardmagConstants;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardmagStoreParserTest {
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
    @Mock
    private CardmagTypesResolver typesResolver;

    @InjectMocks
    private CardmagStoreParser parser;


    @Test
    void testParseGameInShopFromPage_whenParsingIsSuccessful_shouldReturnGameInShop() {
        String title = "КАТАЛОГ ПРОДУКЦІЇ Gaming DLCs Test Game - Some Dlc (Xbox One) - Xbox Live Key - UNITED STATES";
        setUpParseGamePage(
                "Test Game - Some DLC (Xbox One) - Xbox Live Key - UNITED STATES",
                "1 200 ₴",
                title
        );
        when(typesResolver.resolveActivationType(title)).thenReturn(ClientActivationType.MICROSOFT);


        GameInShop result = parser.parseGameInShopFromPage(mockDocument);


        assertThat(result).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(GameInShop.builder()
                        .nameInStore("Test Game Some DLC")
                        .price(new BigDecimal("1200"))
                        .discountPrice(new BigDecimal("1200"))
                        .discount(0)
                        .isAvailable(true)
                        .clientType(ClientActivationType.MICROSOFT)
                        .build());
    }

    @Test
    void testGetName_whenGameNameIsPresent_shouldReturnName() {
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_NAME_FIELD)).thenReturn(mockElements);
        when(mockElements.first()).thenReturn(mockElement);
        when(mockElement.text()).thenReturn("Game Name");

        String name = parser.getName(mockDocument);

        assertThat(name).isEqualTo("Game Name");
    }

    @Test
    void testParseSearchResults_whenSearchResultsParsedSuccessfully_shouldReturnListOfUrls() {
        when(pageFetcher.getPage(anyString())).thenReturn(Optional.of(mockDocument));
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_IN_LIST)).thenReturn(mockElements);
        when(mockElements.stream()).thenReturn(List.of(mockElement).stream());
        when(mockElement.attr(Constants.ATTRIBUTE_HREF)).thenReturn("/game-link");

        List<String> results = parser.parseSearchResults("Some Game", pageFetcher);

        assertThat(results).containsExactly("https://cardmag.com.ua/game-link");
    }

    @Test
    void testGetGenres_whenGamePageHasGenres_shouldReturnGenres() {
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_TITLE_CLASS)).thenReturn(mockElements);
        when(mockElements.first()).thenReturn(mockElement);
        when(mockElement.attr(CardmagConstants.GAME_PAGE_TITLE_ATTR)).thenReturn("Action RPG");
        when(typesResolver.resolveGenres("Action RPG")).thenReturn(List.of(Genre.ACTION,Genre.ACTION));


        List<Genre> genres = parser.getGenres(mockDocument);


        assertThat(genres).isNotNull();
    }

    @Test
    void testGetProductType_whenTitleNotContainTypeButDlcElement_shouldReturnAdditionProductType() {
        String title = "КАТАЛОГ ПРОДУКЦІЇ Gaming DLCs Test Game - Some Dlc (Xbox One) - Xbox Live Key - UNITED STATES";
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_TITLE_CLASS)).thenReturn(mockElements);
        when(mockElements.first()).thenReturn(mockElement);
        when(mockElement.attr(CardmagConstants.GAME_PAGE_TITLE_ATTR))
                .thenReturn(title);
        when(typesResolver.resolveProductType(title, mockDocument)).thenReturn(ProductType.ADDITION);


        ProductType productType = parser.getProductType(mockDocument);


        assertThat(productType).isEqualTo(ProductType.ADDITION);
    }

    @Test
    void testGetProductType_whenTitleNotContainType_shouldReturnGameProductType() {
        String title = "КАТАЛОГ ПРОДУКЦІЇ Xbox Live Key - UNITED STATES";
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_TITLE_CLASS)).thenReturn(mockElements);
        when(mockElements.first()).thenReturn(mockElement);
        when(mockElement.attr(CardmagConstants.GAME_PAGE_TITLE_ATTR)).thenReturn(title);
        when(typesResolver.resolveProductType(title, mockDocument)).thenReturn(ProductType.GAME);


        ProductType productType = parser.getProductType(mockDocument);


        assertThat(productType).isEqualTo(ProductType.GAME);
    }

    @Test
    void testSaveImage_whenImageIsAvailable_shouldSaveImage() {
        String url = "http://image.url/game.jpg";
        String name = "Game Name";

        Element nameElement = mock(Element.class);
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_IMG_CLASS)).thenReturn(new Elements(mockElement));
        when(mockElement.attr("src")).thenReturn(url);
        when(mockElement.attr("alt")).thenReturn("Game Name, фото 1");
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_NAME_FIELD)).thenReturn(new Elements(nameElement));
        when(nameElement.text()).thenReturn(name);


        parser.saveImage(mockDocument);


        verify(resourceService).saveImage(url, name);
    }

    @Test
    void testSaveImage_whenNoImageFound_shouldNotSaveImage() {
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_IMG_CLASS)).thenReturn(new Elements());
        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_NAME_FIELD)).thenReturn(mockElements);
        when(mockElements.first()).thenReturn(mockElement);
        when(mockElement.text()).thenReturn("Game Name");

        parser.saveImage(mockDocument);

        verifyNoInteractions(resourceService);
    }

    private void setUpParseGamePage(String name, String price, String title) {
        Element nameElement = mock(Element.class);

        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_NAME_FIELD))
                .thenReturn(new Elements(nameElement));
        when(nameElement.text()).thenReturn(name);


        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_IS_AVAILABLE))
                .thenReturn(new Elements(mock(Element.class)));

        Element priceElement = mock(Element.class);
        Element titleElement = mock(Element.class);

        when(mockDocument.select(CardmagConstants.GAME_PRICE_FIELD))
                .thenReturn(new Elements(priceElement));
        when(priceElement.text()).thenReturn(price);

        when(mockDocument.getElementsByClass(CardmagConstants.GAME_PAGE_TITLE_CLASS))
                .thenReturn(new Elements(titleElement));
        when(titleElement.attr(CardmagConstants.GAME_PAGE_TITLE_ATTR)).thenReturn(title);
    }
}
