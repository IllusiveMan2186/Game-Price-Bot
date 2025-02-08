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
import com.gpb.game.util.store.GamazeyConstants;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("gamazeyParser")
public class GamazeyStoreParser extends AbstractStoreParser implements StoreParser {

    private final ResourceService resourceService;
    private final Map<String, ProductType> productTypeMap;
    private final Map<String, ClientActivationType> clientActivationTypeMap;

    public GamazeyStoreParser(ResourceService resourceService,
                              @Qualifier("gamazeyGenres") Map<String, Genre> genreMap,
                              @Qualifier("gamazeyProductTypes") Map<String, ProductType> productTypeMap,
                              @Qualifier("gamazeyClientActivation") Map<String, ClientActivationType> clientActivationTypeMap) {
        super(genreMap);
        this.resourceService = resourceService;
        this.productTypeMap = productTypeMap;
        this.clientActivationTypeMap = clientActivationTypeMap;
    }

    @Override
    public GameInShop parseGameInShopFromPage(Document page) {
        return GameInShop.builder()
                .nameInStore(getName(page))
                .price(BigDecimal.valueOf(extractInteger(page, GamazeyConstants.GAME_PAGE_OLD_PRICE_FIELD)))
                .discountPrice(BigDecimal.valueOf(extractInteger(page, GamazeyConstants.GAME_PAGE_DISCOUNT_PRICE_FIELD)))
                .discount(extractDiscount(page))
                .isAvailable(isGameAvailable(page))
                .clientType(getClientActivationTypeFromGameName(page))
                .build();
    }

    @Override
    public String getName(Document page) {
        return sanitizeGameName(extractTextFromFirstElement(page, GamazeyConstants.GAME_PAGE_NAME_FIELD));
    }

    @Override
    public List<String> parseSearchResults(String name, StorePageParser pageFetcher) {
        Document page = pageFetcher.getPage(GamazeyConstants.GAMEZEY_SEARCH_URL + name);
        return page.getElementsByClass(GamazeyConstants.GAME_IN_LIST)
                .stream()
                .map(element -> element.child(0).attr(Constants.ATTRIBUTE_HREF))
                .toList();
    }

    @Override
    public List<Genre> getGenres(Document page) {
        return page.getElementsByClass(GamazeyConstants.GAME_PAGE_CHARACTERISTICS)
                .stream()
                .filter(ch -> ch.text().contains(GamazeyConstants.GAME_GENRES_FIELD))
                .findFirst()
                .map(this::getGenres)
                .orElse(Collections.emptyList());
    }

    @Override
    public ProductType getProductType(Document page) {
        String originalName = page.getElementsByClass(GamazeyConstants.GAME_PAGE_NAME_FIELD).text();

        return productTypeMap.entrySet()
                .stream()
                .filter(entry -> originalName.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(ProductType.GAME);
    }

    @Override
    public void saveImage(Document page) {
        String gameName = getName(page);
        List<Element> elements = page.getElementsByClass(GamazeyConstants.GAME_IMG_CLASS);
        if (elements.size() < 2) {
            log.warn("No suitable image found for {}", gameName);
            return;
        }

        String imgUrl = elements.get(1).attr("src");
        resourceService.cropImage(imgUrl, gameName,
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_START, 0,
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_LONG, GamazeyConstants.GAME_IMAGE_HEIGHT);
    }

    private String sanitizeGameName(String nameFromPage) {
        return nameFromPage
                .replaceAll(GamazeyConstants.GAME_NAME_PRODUCT_TYPE_PART, "")
                .replaceAll(GamazeyConstants.GAME_NAME_SPECIFICATION_PART, "")
                .replaceAll(GamazeyConstants.GAME_NAME_PROBLEMATIC_SYMBOLS, "-");

    }

    private ClientActivationType getClientActivationTypeFromGameName(Document page) {
        String originalName = extractTextFromFirstElement(page, GamazeyConstants.GAME_PAGE_NAME_FIELD);
        return clientActivationTypeMap.entrySet()
                .stream()
                .filter(entry -> originalName.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private boolean isGameAvailable(Document page) {
        return page.getElementsByClass(GamazeyConstants.GAME_PAGE_IS_AVAILABLE).isEmpty();
    }

    private int extractInteger(Document page, String field) {
        String text = extractTextFromFirstElement(page, field);
        String sanitized = text.replaceAll("\\D", "");
        return sanitized.isEmpty() ? 0 : Integer.parseInt(sanitized);
    }
    private int extractDiscount(Document page) {
        String text = page.getElementById(GamazeyConstants.GAME_PAGE_DISCOUNT_FIELD).text();
        String sanitized = text.replaceAll("\\D", "");
        return sanitized.isEmpty() ? 0 : Integer.parseInt(sanitized);
    }
}
