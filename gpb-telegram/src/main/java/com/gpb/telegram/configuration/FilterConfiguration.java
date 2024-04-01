package com.gpb.telegram.configuration;

import com.gpb.telegram.filter.FilterChain;
import com.gpb.telegram.filter.TelegramFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FilterConfiguration {

    private final List<TelegramFilter> filters;

    @Autowired
    public FilterConfiguration(List<TelegramFilter> filters) {
        this.filters = filters;
    }

    @Bean
    public FilterChain createFilterChain() {
        TelegramFilter firstFilter = filters.get(0);
        TelegramFilter filter = firstFilter;
        for (int i = 1; i < filters.size(); i++) {
            filter.setNextFilter(filters.get(i));
            filter = filters.get(i);
        }
        return new FilterChain(firstFilter);
    }
}
