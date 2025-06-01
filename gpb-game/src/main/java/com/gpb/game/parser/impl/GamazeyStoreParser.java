package com.gpb.game.parser.impl;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.parser.AbstractStoreParser;
import com.gpb.game.parser.StorePageParser;
import com.gpb.game.parser.StoreParser;
import com.gpb.game.resolver.store.GamazeyTypesResolver;
import com.gpb.game.service.ResourceService;
import com.gpb.game.util.Constants;
import com.gpb.game.util.store.GamazeyConstants;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("gamazeyParser")
public class GamazeyStoreParser extends AbstractStoreParser implements StoreParser {

    private final ResourceService resourceService;
    private final GamazeyTypesResolver typesResolver;

    public GamazeyStoreParser(ResourceService resourceService, GamazeyTypesResolver typesResolver) {
        this.resourceService = resourceService;
        this.typesResolver = typesResolver;
    }

    @Override
    public GameInShop parseGameInShopFromPage(Document page) {
        return GameInShop.builder()
                .nameInStore(getName(page))
                .price(extractOldPrice(page, GamazeyConstants.GAME_PAGE_OLD_PRICE_FIELD))
                .discountPrice(extractDiscountPrice(page))
                .discount(extractDiscount(page))
                .isAvailable(isGameAvailable(page))
                .clientType(resolveActivationType(page))
                .build();
    }

    @Override
    public String getName(Document page) {
        return sanitizeGameName(extractTextFromFirstElement(page, GamazeyConstants.GAME_PAGE_NAME_FIELD));
    }

    @Override
    public List<String> parseSearchResults(String name, StorePageParser pageFetcher) {
        Optional<Document> page = pageFetcher.getPage(GamazeyConstants.GAMEZEY_SEARCH_URL + name);
        if (page.isEmpty()) return Collections.emptyList();

        return page.get().getElementsByClass(GamazeyConstants.GAME_IN_LIST)
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
                .map(el -> typesResolver.resolveGenres(el.text()))
                .orElse(Collections.emptyList());
    }

    @Override
    public ProductType getProductType(Document page) {
        String rawName = page.getElementsByClass(GamazeyConstants.GAME_PAGE_NAME_FIELD).text();
        return typesResolver.resolveProductType(rawName);
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
        resourceService.saveCroppedImage(imgUrl, gameName,
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_START, 0,
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_LONG, GamazeyConstants.GAME_IMAGE_HEIGHT);
    }

    private String sanitizeGameName(String nameFromPage) {
        return nameFromPage
                .replaceAll(GamazeyConstants.GAME_NAME_PRODUCT_TYPE_PART, "")
                .replaceAll(GamazeyConstants.GAME_NAME_SPECIFICATION_PART, "")
                .replaceAll(GamazeyConstants.GAME_NAME_PROBLEMATIC_SYMBOLS, "");
    }

    private ClientActivationType resolveActivationType(Document page) {
        String rawName = extractTextFromFirstElement(page, GamazeyConstants.GAME_PAGE_NAME_FIELD);
        return typesResolver.resolveActivationType(rawName);
    }

    private boolean isGameAvailable(Document page) {
        return page.getElementsByClass(GamazeyConstants.GAME_PAGE_IS_AVAILABLE).isEmpty();
    }

    private BigDecimal extractOldPrice(Document page, String field) {
        String text = extractTextFromFirstElement(page, field);
        String sanitized = text.replaceAll("\\D", "");
        return BigDecimal.valueOf(sanitized.isEmpty() ? 0 : Integer.parseInt(sanitized));
    }

    private BigDecimal extractDiscountPrice(Document page) {
        String text = page.getElementsByClass(GamazeyConstants.GAME_PAGE_DISCOUNT_PRICE_FIELD).get(0)
                .child(1).text();
        String sanitized = text.replaceAll("\\D", "");
        return BigDecimal.valueOf(sanitized.isEmpty() ? 0 : Integer.parseInt(sanitized));
    }

    private int extractDiscount(Document page) {
        String text = page.getElementById(GamazeyConstants.GAME_PAGE_DISCOUNT_FIELD).text();
        String sanitized = text.replaceAll("\\D", "");
        return sanitized.isEmpty() ? 0 : Integer.parseInt(sanitized);
    }
}
