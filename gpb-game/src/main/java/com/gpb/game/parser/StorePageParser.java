package com.gpb.game.parser;

import com.gpb.common.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Parser for retrieving and parsing HTML content from store pages.
 * <p>
 * This component uses <a href="https://jsoup.org/">Jsoup</a> to connect to a specified URL,
 * fetch the HTML content, and parse it into a {@link Document} object. If the URL cannot be reached
 * or the content is unavailable, a {@link NotFoundException} is thrown.
 * </p>
 */
@Slf4j
@Component
public class StorePageParser {

    /**
     * Retrieves and parses the HTML page from the specified URL.
     *
     * @param url the URL of the store page to be fetched; must be a valid URL.
     * @return a {@link Document} object representing the parsed HTML content of the page.
     * @throws NotFoundException if an I/O error occurs while fetching the page (e.g., if the URL is invalid or the page is inaccessible).
     */
    public Document getPage(final String url) {
        try {
            log.info("Get page from url {}", url);
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.5481.77 Safari/537.36")
                    .get();
        } catch (MalformedURLException e) {
            log.error("Page with url '{}' not found. MalformedURLException:{}", url, e);
            throw new NotFoundException("app.game.error.url.not.found");
        } catch (IOException e) {
            log.error("Page with url '{}' not found. IOException:{}", url, e);
            throw new NotFoundException("app.game.error.url.not.found");
        }
    }
}
