package com.gpb.game.parser.impl;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.parser.AbstractStoreParser;
import com.gpb.game.parser.StorePageParser;
import com.gpb.game.parser.StoreParser;
import com.gpb.game.service.ResourceService;
import com.gpb.game.util.Constants;
import com.gpb.game.util.store.CardmagConstants;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("cardmagParser")
public class CardmagStoreParser extends AbstractStoreParser implements StoreParser {

    private final ResourceService resourceService;
    private final Map<String, ProductType> productTypeMap;
    private final Map<String, ClientActivationType> clientActivationTypeMap;

    public CardmagStoreParser(ResourceService resourceService,
                              @Qualifier("cardmagGeners") Map<String, Genre> genreMap,
                              @Qualifier("cardmagProductTypes") Map<String, ProductType> productTypeMap,
                              @Qualifier("cardmagClientActivation") Map<String, ClientActivationType> clientActivationTypeMap) {
        super(genreMap);
        this.resourceService = resourceService;
        this.productTypeMap = productTypeMap;
        this.clientActivationTypeMap = clientActivationTypeMap;
    }

    @Override
    public GameInShop parseGameInShopFromPage(Document page) {
        BigDecimal price = BigDecimal.valueOf(extractInteger(page));
        return GameInShop.builder()
                .nameInStore(getName(page))
                .price(price)
                .discountPrice(price)
                .isAvailable(isAvailable(page))
                .clientType(getClientActivationTypeFromGameName(page))
                .build();
    }

    @Override
    public String getName(Document page) {
        return sanitizeGameName(extractTextFromFirstElement(page, CardmagConstants.GAME_PAGE_NAME_FIELD));
    }

    @Override
    public List<String> parseSearchResults(String name, StorePageParser pageFetcher) {
        Document page = pageFetcher.getPage(CardmagConstants.CARDMAQ_SEARCH_URL + name);
        return page.getElementsByClass(CardmagConstants.GAME_IN_LIST)
                .stream()
                .map(element -> CardmagConstants.CARDMAQ_HOST_URL.concat(element.attr(Constants.ATTRIBUTE_HREF)))
                .toList();
    }

    @Override
    public List<Genre> getGenres(Document page) {
        String title = getTitle(page);
        return genreMap.entrySet()
                .stream()
                .filter(entry -> title.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }

    @Override
    public ProductType getProductType(Document page) {
        String title = getTitle(page);

        return productTypeMap.entrySet().stream()
                .filter(entry -> title.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseGet(() -> page.getElementsByClass(CardmagConstants.DLC_FIELD)
                        .stream()
                        .findAny()
                        .map(el -> ProductType.ADDITION)
                        .orElse(ProductType.GAME));
    }

    @Override
    public void saveImage(Document page) {
        Elements elements = page.getElementsByClass(CardmagConstants.GAME_IMG_CLASS);
        String gameName = getName(page);

        for (Element element : elements) {
            String altText = element.attr("alt");
            if (altText.contains(CardmagConstants.MAIN_GAME_IMG_MARK)) {
                String imgUrl = element.attr("src");
                resourceService.saveImage(imgUrl, gameName);
                return;
            }
        }

        log.warn("Image not found for game: {}", gameName);
    }

    private String sanitizeGameName(String nameFromPage) {
        return nameFromPage
                .replaceAll(CardmagConstants.GAME_NAME_EXTRA_PART, "")
                .replaceAll(CardmagConstants.GAME_NAME_PROBLEMATIC_SYMBOLS, "");
    }

    private ClientActivationType getClientActivationTypeFromGameName(Document page) {
        String title = getTitle(page);

        return clientActivationTypeMap.entrySet()
                .stream()
                .filter(entry -> title.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private String getTitle(Document page) {
        return page
                .getElementsByClass(CardmagConstants.GAME_PAGE_TITLE_CLASS)
                .first()
                .attr(CardmagConstants.GAME_PAGE_TITLE_ATTR);
    }

    private boolean isAvailable(Document page) {
        return page.getElementsByClass(CardmagConstants.GAME_PAGE_IS_AVAILABLE) != null;
    }

    private int extractInteger(Document page) {
        String text = page.select(CardmagConstants.GAME_PRICE_FIELD).text();
        String sanitized = text.replaceAll("\\D", "");
        return sanitized.isEmpty() ? 0 : Integer.parseInt(sanitized);
    }
}
