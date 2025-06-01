package com.gpb.game.unit.resolver;

import com.gpb.common.exception.NotFoundException;
import com.gpb.game.resolver.StoreServiceResolver;
import com.gpb.game.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class StoreServiceResolverTest {

    private StoreService store1;
    private StoreService store2;
    private StoreServiceResolver resolver;

    @BeforeEach
    void setUp() {
        store1 = mock(StoreService.class);
        store2 = mock(StoreService.class);

        Map<String, StoreService> serviceMap = new HashMap<>();
        serviceMap.put("example.com", store1);
        serviceMap.put("test.com", store2);

        resolver = new StoreServiceResolver(serviceMap);
    }

    @Test
    void testGetByUrl_whenValidHost_shouldReturnService() {
        StoreService service = resolver.getByUrl("http://example.com/game/1");
        assertThat(service).isEqualTo(store1);
    }

    @Test
    void testGetByUrl_whenInvalidHost_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> resolver.getByUrl("http://unknown.com/game/1"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("app.game.error.host.not.supported");
    }

    @Test
    void testGetByUrl_whenMalformedUrl_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> resolver.getByUrl("ht!tp:/malformed-url"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("app.game.error.url.not.found");
    }

    @Test
    void testGetByHost_whenExistingHost_shouldReturnService() {
        assertThat(resolver.getByHost("test.com")).isEqualTo(store2);
    }

    @Test
    void testGetByHost_whenNonExistingHost_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> resolver.getByHost("invalid.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("app.game.error.host.not.supported");
    }

    @Test
    void testGetAllHosts_whenSuccess_shouldReturnAllKeys() {
        Set<String> hosts = resolver.getAllHosts();
        assertThat(hosts).containsExactlyInAnyOrder("example.com", "test.com");
    }

    @Test
    void testGetAllExcept_whenSuccess_shouldReturnAllButSkipped() {
        Collection<StoreService> services = resolver.getAllExcept(store1);
        assertThat(services).containsOnly(store2);
    }

    @Test
    void testGetAllServices_whenSuccess_shouldReturnAll() {
        Collection<StoreService> services = resolver.getAllServices();
        assertThat(services).containsExactlyInAnyOrder(store1, store2);
    }
}