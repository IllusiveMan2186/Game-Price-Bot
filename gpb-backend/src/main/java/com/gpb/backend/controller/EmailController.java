package com.gpb.backend.controller;


import com.gpb.backend.entity.EmailChanging;
import com.gpb.backend.entity.dto.EmailRequestDto;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.exception.EmailAlreadyExistException;
import com.gpb.backend.exception.UserDataNotChangedException;
import com.gpb.backend.service.EmailChangingService;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserManagementService;
import com.gpb.common.entity.user.TokenRequestDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final UserManagementService userManagementService;
    private final UserActivationService userActivationService;
    private final EmailChangingService emailChangingService;
    private final EmailService emailService;

    /**
     * Updates the email address for the authenticated user.
     *
     * <p>This endpoint allows a registered user to update their email by providing the new email address
     * in the request body. The user's email is updated using the authentication service, and a new token
     * is generated based on the updated email address.</p>
     *
     * @param emailRequestDto the DTO containing the new email information
     * @param user            the authenticated user's details
     */
    @PatchMapping
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserEmail(@RequestBody final EmailRequestDto emailRequestDto, @AuthenticationPrincipal UserDto user) {
        log.info("User with ID {} requested email change", user.getId());

        String newEmail = emailRequestDto.getEmail();
        if (newEmail.equals(user.getEmail())) {
            throw new UserDataNotChangedException();
        }
        if (userManagementService.getWebUserByEmail(newEmail).isPresent()) {
            log.debug("Email already registered in system while update email attempt for user {}", user.getId());
            throw new EmailAlreadyExistException();
        }

        EmailChanging emailChanging = emailChangingService
                .createEmailChanging(newEmail, userManagementService.getWebUserById(user.getId()));
        emailService.sendEmailChange(emailChanging);

        log.debug("User with ID {} successfully updated email", user.getId());
    }

    /**
     * Resend the activation email to the user
     *
     * @param emailRequestDto email for resending activation message
     */
    @PostMapping(value = "/resend")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendUserActivationEmail(@RequestBody final EmailRequestDto emailRequestDto) {
        log.debug("Resending activation email for a user");

        userActivationService.resendActivationEmail(emailRequestDto.getEmail());

        log.debug("Activation email resent successfully");
    }

    /**
     * Handles email confirmation request.
     *
     * @param tokenRequestDto the request containing the confirmation token
     * @return the response message indicating the confirmation status
     */
    @PostMapping(value = "/change/confirm")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public String emailConformation(@RequestBody final TokenRequestDto tokenRequestDto) {
        log.debug("New email conformation");

        String response = emailChangingService.confirmEmailChangingToken(tokenRequestDto.getToken());

        log.debug("New successfully confirmed");
        return response;
    }
}
