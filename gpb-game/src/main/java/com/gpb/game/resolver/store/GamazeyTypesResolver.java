package com.gpb.game.resolver.store;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Resolver for mapping text data from the Gamazey store
 * to enum representations .
 */
@Component
public class GamazeyTypesResolver {

    private final Map<String, ProductType> productTypeMap;
    private final Map<String, ClientActivationType> clientActivationTypeMap;
    private final Map<String, Genre> genreMap;

    public GamazeyTypesResolver(
            @Qualifier("gamazeyGenres") Map<String, Genre> genreMap,
            @Qualifier("gamazeyProductTypes") Map<String, ProductType> productTypeMap,
            @Qualifier("gamazeyClientActivation") Map<String, ClientActivationType> clientActivationTypeMap) {
        this.genreMap = genreMap;
        this.productTypeMap = productTypeMap;
        this.clientActivationTypeMap = clientActivationTypeMap;
    }

    /**
     * Resolves a {@link ProductType} from a given text based on partial keyword matching.
     * If no match is found, {@link ProductType#GAME} is returned as a default.
     *
     * @param name the raw product name
     * @return the resolved {@link ProductType}, or {@code GAME} by default
     */
    public ProductType resolveProductType(String name) {
        return productTypeMap.entrySet().stream()
                .filter(e -> name.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(ProductType.GAME);
    }

    /**
     * Resolves a {@link ClientActivationType} from a given text based on partial keyword matching.
     *
     * @param name the raw product name
     * @return the resolved {@link ClientActivationType}, or {@code null} if no match is found
     */
    public ClientActivationType resolveActivationType(String name) {
        return clientActivationTypeMap.entrySet().stream()
                .filter(e -> name.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Resolves a list of {@link Genre} values from a given text, based on keyword matches.
     *
     * @param text the raw text description
     * @return a distinct list of matching {@link Genre} values, possibly empty
     */
    public List<Genre> resolveGenres(String text) {
        return genreMap.entrySet().stream()
                .filter(e -> text.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .distinct()
                .toList();
    }
}
