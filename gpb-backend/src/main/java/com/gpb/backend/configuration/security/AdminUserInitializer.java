package com.gpb.backend.configuration.security;

import com.gpb.backend.entity.WebUser;
import com.gpb.backend.repository.WebUserRepository;
import com.gpb.common.entity.user.NotificationRequestDto;
import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.common.service.RestTemplateHandlerService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Initializes a default admin user at application startup if one does not already exist.
 * <p>
 * This component listens for the {@link ContextRefreshedEvent} and checks if an admin user with the configured
 * email exists. If not, it creates a new admin user using the provided configuration properties and saves it
 * into the repository.
 * </p>
 */
@Slf4j
@Data
@Component
public class AdminUserInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final WebUserRepository userRepository;
    private final RestTemplateHandlerService restTemplateHandler;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    /**
     * Constructs an {@code AdminUserInitializer} with required dependencies.
     *
     * @param userRepository      the repository used for accessing web users
     * @param restTemplateHandler the service used to execute REST requests
     * @param passwordEncoder     the encoder used for password encryption
     */
    public AdminUserInitializer(final WebUserRepository userRepository,
                                final RestTemplateHandlerService restTemplateHandler,
                                final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.restTemplateHandler = restTemplateHandler;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Called when the application context is refreshed.
     * <p>
     * Checks if an admin user exists by searching for the configured admin email. If not found, creates a new admin user.
     * </p>
     *
     * @param event the context refreshed event
     */
    @Override
    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000) // Retry every 2 seconds, up to 5 times
    )
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        log.debug("Check default admin user");
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            final Long basicUserId = restTemplateHandler.executeRequestWithBody(
                    "/user",
                    HttpMethod.POST,
                    null,
                    new NotificationRequestDto(UserNotificationType.EMAIL),
                    Long.class
            );

            final WebUser admin = WebUser.builder()
                    .id(1L)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .locale(new Locale("en"))
                    .role("ROLE_ADMIN")
                    .isActivated(true)
                    .basicUserId(basicUserId)
                    .build();

            userRepository.save(admin);
            log.info("Admin user with email '{}' has been created successfully.", adminEmail);
        } else {
            log.info("Admin user with email '{}' already exists. No initialization required.", adminEmail);
        }
    }
}
