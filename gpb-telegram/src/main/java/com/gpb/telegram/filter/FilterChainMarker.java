package com.gpb.telegram.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark classes that participate in a filter chain.
 * <p>
 * The {@code FilterChainMarker} annotation allows you to specify one or more filter keys that
 * determine which filters should be applied when processing a Telegram request.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FilterChainMarker {

    /**
     * An array of filter keys.
     * <p>
     * The keys specified here are used in the filter chain to match and apply appropriate filters.
     * </p>
     *
     * @return an array of {@link String} keys that define which filters to apply.
     */
    String[] value();
}
