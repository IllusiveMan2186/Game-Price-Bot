package com.gpb.game.resolver;

import com.gpb.common.exception.NotFoundException;
import com.gpb.game.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A resolver component responsible for determining the appropriate {@link StoreService}
 * implementation based on the given URL or host.
 */
@Component
@Slf4j
public class StoreServiceResolver {

    private final Map<String, StoreService> storeServices;

    public StoreServiceResolver(Map<String, StoreService> storeServices) {
        this.storeServices = storeServices;
    }

    /**
     * Retrieves a {@link StoreService} based on the host part of the given URL string.
     *
     * @param link a valid URL string (e.g., "https://store.example.com/product/123")
     * @return the corresponding {@link StoreService} implementation
     * @throws NotFoundException if the URL is malformed or the host is not supported
     */
    public StoreService getByUrl(String link) {
        try {
            URL url = new URL(link);
            String host = url.getHost();
            StoreService storeService = storeServices.get(host);
            if (storeService != null) {
                return storeService;
            }
            log.error("Host '{}' not supported", host);
            throw new NotFoundException("app.game.error.host.not.supported");
        } catch (MalformedURLException e) {
            log.error("Malformed URL '{}'", link, e);
            throw new NotFoundException("app.game.error.url.not.found");
        }
    }

    /**
     * Returns a set of all supported store hostnames.
     *
     * @return a set of host strings
     */
    public Set<String> getAllHosts() {
        return storeServices.keySet();
    }

    /**
     * Retrieves all {@link StoreService} implementations except the one provided in the argument.
     *
     * @param skip the {@link StoreService} to exclude from the result
     * @return a collection of all other {@link StoreService} instances
     */
    public Collection<StoreService> getAllExcept(StoreService skip) {
        return storeServices.values().stream()
                .filter(service -> !service.equals(skip))
                .toList();
    }

    /**
     * Retrieves a {@link StoreService} directly by host string.
     *
     * @param host the exact hostname (e.g., "store.example.com")
     * @return the corresponding {@link StoreService}
     * @throws NotFoundException if no service is registered for the given host
     */
    public StoreService getByHost(String host) {
        StoreService storeService = storeServices.get(host);
        if (storeService == null) {
            throw new NotFoundException("app.game.error.host.not.supported");
        }
        return storeService;
    }

    /**
     * Returns all registered {@link StoreService} implementations.
     *
     * @return a collection of all {@link StoreService} instances
     */
    public Collection<StoreService> getAllServices() {
        return storeServices.values();
    }
}
