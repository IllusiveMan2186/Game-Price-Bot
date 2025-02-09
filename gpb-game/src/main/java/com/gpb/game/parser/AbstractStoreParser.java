package com.gpb.game.parser;

import com.gpb.common.entity.game.Genre;
import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Map;

@AllArgsConstructor
public abstract class AbstractStoreParser {

    protected final Map<String, Genre> genreMap;

    protected String extractTextFromFirstElement(Document page, String className) {
        Element element = page.getElementsByClass(className).first();
        return (element != null) ? element.text() : "";

    }
}
