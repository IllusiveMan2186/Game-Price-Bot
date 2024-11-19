package com.gpb.web.configuration;

import com.gpb.web.bean.user.BasicUser;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.repository.UserRepository;
import com.gpb.web.repository.WebUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Create default admin user after tablets creation by jpa
 */
@Slf4j
@Component
public class DefaultAdminInitializer implements CommandLineRunner {

    private final WebUserRepository webUserRepository;
    private final UserRepository basicUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    public DefaultAdminInitializer(WebUserRepository webUserRepository,
                                   UserRepository basicUserRepository,
                                   PasswordEncoder passwordEncoder) {
        this.webUserRepository = webUserRepository;
        this.basicUserRepository = basicUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create default admin user with variables credentials if it does not exist
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        if (!webUserRepository.existsById(1L) && webUserRepository.findByEmail(adminEmail).isEmpty()) {
            BasicUser basicUser = new BasicUser();
            basicUser.setId(1L);
            basicUserRepository.save(basicUser);

            WebUser admin = WebUser.builder()
                    .id(1L)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .locale(new Locale("en"))
                    .role("ROLE_ADMIN")
                    .isActivated(true)
                    .basicUser(basicUser)
                    .build();
            webUserRepository.save(admin);

            log.info("Admin user created");
        }
    }
}