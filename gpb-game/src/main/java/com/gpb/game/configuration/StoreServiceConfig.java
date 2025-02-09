package com.gpb.game.configuration;

import com.gpb.game.parser.StorePageParser;
import com.gpb.game.parser.StoreParser;
import com.gpb.game.service.impl.store.CommonStoreServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreServiceConfig {

    @Bean(name = "gamazey.com.ua")
    public CommonStoreServiceImpl gamazeyStoreService(StorePageParser pageFetcher,
                                                      @Qualifier("gamazeyParser") StoreParser storeParser) {
        return new CommonStoreServiceImpl(pageFetcher, storeParser);
    }

    @Bean(name = "cardmag.com.ua")
    public CommonStoreServiceImpl anotherStoreService(StorePageParser pageFetcher,
                                                      @Qualifier("cardmagParser") StoreParser storeParser) {
        return new CommonStoreServiceImpl(pageFetcher, storeParser);
    }
}
