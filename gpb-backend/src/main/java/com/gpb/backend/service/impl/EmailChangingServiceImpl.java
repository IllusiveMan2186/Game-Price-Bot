package com.gpb.backend.service.impl;

import com.gpb.backend.entity.EmailChanging;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.exception.TokenExpireException;
import com.gpb.backend.repository.EmailChangingRepository;
import com.gpb.backend.service.EmailChangingService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.common.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class EmailChangingServiceImpl implements EmailChangingService {

    private final UserAuthenticationService userActivationService;
    private final EmailChangingRepository emailChangingRepository;

    @Override
    public EmailChanging createEmailChanging(String newEmail, WebUser user) {
        EmailChanging emailChanging = EmailChanging.builder()
                .newEmail(newEmail)
                .user(user)
                .oldEmailToken(UUID.randomUUID().toString())
                .newEmailToken(UUID.randomUUID().toString())
                .expirationTime(LocalDateTime.now().plusHours(1))
                .build();

        emailChangingRepository.deleteByUserId(user.getId());
        emailChangingRepository.flush();
        return emailChangingRepository.save(emailChanging);
    }

    @Override
    public String confirmEmailChangingToken(String confirmationToken) {

        EmailChanging emailChanging = getEmailChangingByToken(confirmationToken);

        if (confirmationToken.equals(emailChanging.getNewEmailToken())) {
            emailChanging.setNewEmailConfirmed(true);
            log.info("New email confirmed for user {}", emailChanging.getUser().getId());
        } else {
            emailChanging.setOldEmailConfirmed(true);
            log.info("Old email confirmed for user {}", emailChanging.getUser().getId());
        }

        return updateEmailChangingStatus(emailChanging);
    }

    private EmailChanging getEmailChangingByToken(String confirmationToken){
        Optional<EmailChanging> emailChangingOpt = emailChangingRepository
                .findByNewEmailToken(confirmationToken)
                .or(() -> emailChangingRepository.findByOldEmailToken(confirmationToken));

        if (emailChangingOpt.isEmpty()) {
            throw new NotFoundException("app.email.change.token.not.found");
        }

        EmailChanging emailChanging = emailChangingOpt.get();

        if (emailChanging.getExpirationTime().isBefore(LocalDateTime.now())) {
            log.warn("Token expired for email change request {}", emailChanging.getId());
            emailChangingRepository.deleteById(emailChanging.getId());
            throw new TokenExpireException();
        }
        return emailChanging;
    }

    private String updateEmailChangingStatus(EmailChanging emailChanging) {
        if (emailChanging.isOldEmailConfirmed() && emailChanging.isNewEmailConfirmed()) {
            userActivationService.updateUserEmail(emailChanging.getNewEmail(), emailChanging.getUser());
            emailChangingRepository.deleteById(emailChanging.getId());
            return "app.email.change.success.message";
        } else {
            emailChangingRepository.save(emailChanging);
            return "app.email.change.confirm.success.message";
        }
    }
}
