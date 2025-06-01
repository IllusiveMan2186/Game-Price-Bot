package com.gpb.game.resolver.store;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.util.store.CardmagConstants;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Resolver for mapping raw text and HTML content from the Cardmag store
 * into internal enum types.
 */
@Component
public class CardmagTypesResolver {

    private final Map<String, Genre> genreMap;
    private final Map<String, ProductType> productTypeMap;
    private final Map<String, ClientActivationType> clientActivationTypeMap;

    public CardmagTypesResolver(
            @Qualifier("cardmagGeners") Map<String, Genre> genreMap,
            @Qualifier("cardmagProductTypes") Map<String, ProductType> productTypeMap,
            @Qualifier("cardmagClientActivation") Map<String, ClientActivationType> clientActivationTypeMap) {
        this.genreMap = genreMap;
        this.productTypeMap = productTypeMap;
        this.clientActivationTypeMap = clientActivationTypeMap;
    }

    /**
     * Resolves a list of {@link Genre} values from a given string.
     * <p>
     * Matches are based on keywords contained in the input text.
     * </p>
     *
     * @param text raw string potentially containing genre indicators
     * @return a distinct list of matched {@link Genre} values (might be empty)
     */
    public List<Genre> resolveGenres(String text) {
        return genreMap.entrySet().stream()
                .filter(e -> text.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .distinct()
                .toList();
    }

    /**
     * Resolves a {@link ProductType} based on the product title.
     * <p>
     * If no keyword is matched in the title, it checks the presence of a DLC field in the document
     * and returns {@link ProductType#ADDITION} if found; otherwise, defaults to {@link ProductType#GAME}.
     * </p>
     *
     * @param title product title text
     * @param page  HTML document of the product page
     * @return the resolved {@link ProductType}
     */
    public ProductType resolveProductType(String title, Document page) {
        return productTypeMap.entrySet().stream()
                .filter(e -> title.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseGet(() ->
                        page.getElementsByClass(CardmagConstants.DLC_FIELD)
                                .stream()
                                .findAny()
                                .map(el -> ProductType.ADDITION)
                                .orElse(ProductType.GAME)
                );
    }

    /**
     * Resolves a {@link ClientActivationType} based on the product title using keyword matching.
     *
     * @param title product title text
     * @return the resolved {@link ClientActivationType}, or {@code null} if no match is found
     */
    public ClientActivationType resolveActivationType(String title) {
        return clientActivationTypeMap.entrySet().stream()
                .filter(e -> title.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
