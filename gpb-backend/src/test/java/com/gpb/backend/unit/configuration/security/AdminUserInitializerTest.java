package com.gpb.backend.unit.configuration.security;

import com.gpb.backend.configuration.security.AdminUserInitializer;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.repository.WebUserRepository;
import com.gpb.backend.util.Constants;
import com.gpb.common.entity.user.NotificationRequestDto;
import com.gpb.common.service.RestTemplateHandlerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminUserInitializerTest {

    private AdminUserInitializer adminUserInitializer;
    private WebUserRepository userRepository;
    private RestTemplateHandlerService restTemplateHandler;
    private PasswordEncoder passwordEncoder;

    private final String adminEmail = "admin@example.com";
    private final String adminPassword = "adminPass";

    @BeforeEach
    void setUp() {
        userRepository = mock(WebUserRepository.class);
        restTemplateHandler = mock(RestTemplateHandlerService.class);
        passwordEncoder = mock(PasswordEncoder.class);

        adminUserInitializer = new AdminUserInitializer(userRepository, restTemplateHandler, passwordEncoder);

        adminUserInitializer.setAdminEmail(adminEmail);
        adminUserInitializer.setAdminPassword(adminPassword);
    }

    @Test
    void testOnApplicationEvent_whenUserNotExists_shouldCreateAdminUser() {
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(adminPassword)).thenReturn("encodedPassword");
        when(restTemplateHandler.executeRequestWithBody(
                eq("/user"),
                eq(HttpMethod.POST),
                eq(null),
                any(NotificationRequestDto.class),
                eq(Long.class))
        ).thenReturn(100L); // Mock basicUserId response


        adminUserInitializer.onApplicationEvent(mock(ContextRefreshedEvent.class));


        ArgumentCaptor<WebUser> userCaptor = ArgumentCaptor.forClass(WebUser.class);
        verify(userRepository).save(userCaptor.capture());

        WebUser savedUser = userCaptor.getValue();
        assertNotNull(savedUser);
        assertEquals(adminEmail, savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(new Locale("en"), savedUser.getLocale());
        assertEquals(Constants.ADMIN_ROLE, savedUser.getRole());
        assertTrue(savedUser.isActivated());
        assertEquals(100L, savedUser.getBasicUserId());

        verify(passwordEncoder, times(1)).encode(adminPassword);
        verify(restTemplateHandler, times(1)).executeRequestWithBody(
                eq("/user"),
                eq(HttpMethod.POST),
                eq(null),
                any(NotificationRequestDto.class),
                eq(Long.class)
        );
    }

    @Test
    void testOnApplicationEvent_whenUserAlreadyExists_shouldNotCreateAdminUser() {
        WebUser existingAdmin = WebUser.builder()
                .id(1L)
                .email(adminEmail)
                .password("existingEncodedPassword")
                .role(Constants.ADMIN_ROLE)
                .isActivated(true)
                .basicUserId(100L)
                .build();

        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(existingAdmin));


        adminUserInitializer.onApplicationEvent(mock(ContextRefreshedEvent.class));


        verify(userRepository, never()).save(any(WebUser.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(restTemplateHandler, never()).executeRequestWithBody(any(), any(), any(), any(), any());
    }
}

