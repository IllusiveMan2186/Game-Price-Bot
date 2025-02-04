package com.gpb.telegram.configuration;

import com.gpb.telegram.filter.FilterChain;
import com.gpb.telegram.filter.TelegramFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for assembling the Telegram filter chain.
 * <p>
 * This configuration collects all beans implementing {@link TelegramFilter}, links them together to form
 * a chain of responsibility, and exposes a {@link FilterChain} bean.
 * </p>
 */
@Configuration
public class FilterConfiguration {

    private final List<TelegramFilter> filters;

    /**
     * Constructs a new FilterConfiguration with the specified list of Telegram filters.
     *
     * @param filters a list of {@link TelegramFilter} beans that will be chained together
     */
    @Autowired
    public FilterConfiguration(final List<TelegramFilter> filters) {
        this.filters = filters;
    }

    /**
     * Creates a {@link FilterChain} bean by linking the list of Telegram filters.
     * <p>
     * The filters are linked in the order they are provided in the application context.
     * </p>
     *
     * @return a {@link FilterChain} representing the head of the chain of filters
     */
    @Bean
    public FilterChain createFilterChain() {
        if (filters.isEmpty()) {
            throw new IllegalStateException("No Telegram filters available to create a filter chain.");
        }

        final TelegramFilter firstFilter = filters.get(0);
        TelegramFilter currentFilter = firstFilter;

        for (int i = 1; i < filters.size(); i++) {
            final TelegramFilter nextFilter = filters.get(i);
            currentFilter.setNextFilter(nextFilter);
            currentFilter = nextFilter;
        }

        return new FilterChain(firstFilter);
    }
}