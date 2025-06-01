package com.gpb.game.parser.impl;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.parser.AbstractStoreParser;
import com.gpb.game.parser.StorePageParser;
import com.gpb.game.parser.StoreParser;
import com.gpb.game.resolver.store.CardmagTypesResolver;
import com.gpb.game.service.ResourceService;
import com.gpb.game.util.Constants;
import com.gpb.game.util.store.CardmagConstants;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("cardmagParser")
public class CardmagStoreParser extends AbstractStoreParser implements StoreParser {

    private final ResourceService resourceService;
    private final CardmagTypesResolver typesResolver;

    public CardmagStoreParser(ResourceService resourceService, CardmagTypesResolver typesResolver) {
        this.resourceService = resourceService;
        this.typesResolver = typesResolver;
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
        Optional<Document> page = pageFetcher.getPage(CardmagConstants.CARDMAQ_SEARCH_URL + name);
        if (page.isEmpty()) return Collections.emptyList();
        return page.get().getElementsByClass(CardmagConstants.GAME_IN_LIST)
                .stream()
                .map(element -> CardmagConstants.CARDMAQ_HOST_URL.concat(element.attr(Constants.ATTRIBUTE_HREF)))
                .toList();
    }

    @Override
    public List<Genre> getGenres(Document page) {
        String title = extractTitle(page);
        return typesResolver.resolveGenres(title);
    }


    @Override
    public ProductType getProductType(Document page) {
        String title = extractTitle(page);
        return typesResolver.resolveProductType(title, page);
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
        String title = extractTitle(page);
        return typesResolver.resolveActivationType(title);
    }

    private boolean isAvailable(Document page) {
        return page.getElementsByClass(CardmagConstants.GAME_PAGE_IS_AVAILABLE) != null;
    }

    private int extractInteger(Document page) {
        String text = page.select(CardmagConstants.GAME_PRICE_FIELD).text();
        String sanitized = text.replaceAll("\\D", "");
        return sanitized.isEmpty() ? 0 : Integer.parseInt(sanitized);
    }

    private String extractTitle(Document page) {
        return page.getElementsByClass(CardmagConstants.GAME_PAGE_TITLE_CLASS)
                .first()
                .attr(CardmagConstants.GAME_PAGE_TITLE_ATTR);
    }
}
