package com.gpb.game.configuration;

import com.gpb.game.util.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfiguration {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Constants.THREAD_POOL_CORE_SIZE);
        executor.setMaxPoolSize(Constants.THREAD_POOL_MAX_SIZE);
        executor.setQueueCapacity(Constants.THREAD_POOL_QUEUE_CAPACITY);
        executor.setThreadNamePrefix(Constants.THREAD_POOL_NAME_PREFIX);
        executor.initialize();
        return executor;
    }
}
