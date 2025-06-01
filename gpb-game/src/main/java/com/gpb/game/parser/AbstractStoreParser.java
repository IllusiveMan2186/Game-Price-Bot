package com.gpb.game.parser;

import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@AllArgsConstructor
public abstract class AbstractStoreParser {

    protected String extractTextFromFirstElement(Document page, String className) {
        Element element = page.getElementsByClass(className).first();
        return (element != null) ? element.text() : "";

    }
}
