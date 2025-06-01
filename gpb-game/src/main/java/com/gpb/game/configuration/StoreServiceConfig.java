package com.gpb.game.configuration;

import com.gpb.game.parser.StorePageParser;
import com.gpb.game.parser.StoreParser;
import com.gpb.game.service.impl.store.CommonStoreServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class StoreServiceConfig {

    @Bean(name = "gamazey.com.ua")
    public CommonStoreServiceImpl gamazeyStoreService(StorePageParser pageFetcher,
                                                      @Qualifier("gamazeyParser") StoreParser storeParser,
                                                      ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return new CommonStoreServiceImpl(pageFetcher, storeParser, threadPoolTaskExecutor);
    }

    @Bean(name = "cardmag.com.ua")
    public CommonStoreServiceImpl anotherStoreService(StorePageParser pageFetcher,
                                                      @Qualifier("cardmagParser") StoreParser storeParser,
                                                      ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return new CommonStoreServiceImpl(pageFetcher, storeParser, threadPoolTaskExecutor);
    }
}
