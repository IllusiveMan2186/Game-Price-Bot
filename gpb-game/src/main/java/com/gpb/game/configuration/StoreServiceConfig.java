package com.gpb.game.configuration;

import com.gpb.game.parser.StorePageParser;
import com.gpb.game.parser.StoreParser;
import com.gpb.game.service.impl.store.StoreServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreServiceConfig {

    @Bean(name = "gamazey.com.ua")
    public StoreServiceImpl gamazeyStoreService(StorePageParser pageFetcher,
                                                @Qualifier("gamazeyParser") StoreParser storeParser) {
        return new StoreServiceImpl(pageFetcher, storeParser);
    }

    @Bean(name = "cardmag.com.ua")
    public StoreServiceImpl anotherStoreService(StorePageParser pageFetcher,
                                                @Qualifier("cardmagParser") StoreParser storeParser) {
        return new StoreServiceImpl(pageFetcher, storeParser);
    }
}
