package com.gpb.web.service.impl;

import com.gpb.web.bean.user.UserActivation;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.exception.NotExistingUserActivationTokenException;
import com.gpb.web.repository.UserActivationRepository;
import com.gpb.web.service.EmailService;
import com.gpb.web.service.UserActivationService;
import com.gpb.web.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserActivationServiceImpl implements UserActivationService {

    private final UserActivationRepository userActivationRepository;

    private final UserService userService;
    private final EmailService emailService;

    public UserActivationServiceImpl(UserActivationRepository userActivationRepository, UserService userService, EmailService emailService) {
        this.userActivationRepository = userActivationRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public UserActivation createUserActivation(WebUser user) {
        log.info(String.format("Create activation token for user : %s", user.getId()));

        UserActivation userActivation = UserActivation.builder()
                .user(user)
                .build();
        return userActivationRepository.save(userActivation);
    }

    @Override
    public void resendActivationEmail(String email) {
        log.info(String.format("Resend the activation email to the user for user: %s", email));

        WebUser user = userService.getWebUserByEmail(email);
        UserActivation userActivation = userActivationRepository.findByUser(user);
        emailService.sendEmailVerification(userActivation);
    }

    @Override
    public void activateUserAccount(String token) {
        log.info(String.format("Activate user for token : %s", token));

        UserActivation userActivation = userActivationRepository.findByToken(token);
        if (userActivation == null) {
            throw new NotExistingUserActivationTokenException();
        }
        userService.activateUser(userActivation.getUser().getId());
        userActivationRepository.deleteById(token);
    }
}
