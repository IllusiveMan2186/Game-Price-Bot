package com.gpb.stores.parser;

import com.gpb.stores.exception.NotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StorePageParser {

    public Document getPage(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new NotFoundException("app.game.error.url.not.found");
        }
    }
}
