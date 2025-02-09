package com.gpb.game.parser;

import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.entity.game.GameInShop;
import org.jsoup.nodes.Document;

import java.util.List;

public interface StoreParser {

    GameInShop parseGameInShopFromPage(Document page);
    String getName(Document page);
    List<String> parseSearchResults(String name, StorePageParser pageFetcher);
    List<Genre> getGenres(Document page);
    ProductType getProductType(Document page);
    void saveImage(Document page);
}
