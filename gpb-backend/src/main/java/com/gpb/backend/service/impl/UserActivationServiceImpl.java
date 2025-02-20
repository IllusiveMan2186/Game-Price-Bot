package com.gpb.backend.service.impl;

import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.exception.NotExistingUserActivationTokenException;
import com.gpb.backend.repository.UserActivationRepository;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserManagementService;
import com.gpb.common.exception.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserActivationServiceImpl implements UserActivationService {

    private final UserActivationRepository userActivationRepository;
    private final UserManagementService userService;
    private final EmailService emailService;

    public UserActivationServiceImpl(UserActivationRepository userActivationRepository,
                                     UserManagementService userService,
                                     EmailService emailService) {
        this.userActivationRepository = userActivationRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public UserActivation createUserActivation(WebUser user) {
        log.info("Create activation token for user : {}", user.getId());

        UserActivation userActivation = UserActivation.builder()
                .user(user)
                .build();
        return userActivationRepository.save(userActivation);
    }

    @Override
    public void resendActivationEmail(String email) {
        log.debug("Resend the activation email to the user for user");

        WebUser user = userService.getWebUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("app.user.error.email.not.found"));
        UserActivation userActivation = userActivationRepository.findByUser(user);
        emailService.sendEmailVerification(userActivation);
    }

    @Override
    public void activateUserAccount(String token) {
        log.debug("Activate user for token");

        UserActivation userActivation = userActivationRepository.findByToken(token);
        if (userActivation == null) {
            log.warn("User for activation not found ");
            throw new NotExistingUserActivationTokenException();
        }
        userService.activateUser(userActivation.getUser().getId());
        userActivationRepository.deleteById(token);
    }
}
