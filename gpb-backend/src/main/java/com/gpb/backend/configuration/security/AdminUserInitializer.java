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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Create default admin user
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

    public AdminUserInitializer(WebUserRepository userRepository,
                                RestTemplateHandlerService restTemplateHandler,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.restTemplateHandler = restTemplateHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            Long basicUserId = restTemplateHandler.executeRequestWithBody("/user",
                    HttpMethod.POST,
                    null,
                    new NotificationRequestDto(UserNotificationType.EMAIL),
                    Long.class);

            WebUser admin = WebUser.builder()
                    .id(1L)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .locale(new Locale("en"))
                    .role("ROLE_ADMIN")
                    .isActivated(true)
                    .basicUserId(basicUserId)
                    .build();
            userRepository.save(admin);

            log.info("Admin user created");
        }
    }
}