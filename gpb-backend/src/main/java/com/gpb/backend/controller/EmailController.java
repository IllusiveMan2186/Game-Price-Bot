package com.gpb.backend.controller;


import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.entity.Credentials;
import com.gpb.backend.entity.EmailChanging;
import com.gpb.backend.entity.RefreshToken;
import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.UserRegistration;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.EmailRequestDto;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.exception.EmailAlreadyExistException;
import com.gpb.backend.exception.RefreshTokenException;
import com.gpb.backend.exception.UserDataNotChangedException;
import com.gpb.backend.service.EmailChangingService;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.RefreshTokenService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.service.UserManagementService;
import com.gpb.backend.util.Constants;
import com.gpb.backend.util.CookieUtil;
import com.gpb.common.entity.user.TokenRequestDto;
import com.gpb.common.service.UserLinkerService;
import com.gpb.common.util.CommonConstants;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
     * @return the updated {@link UserDto} after the email change
     */
    @PutMapping
    @Transactional
    public void updateUserEmail(@RequestBody final EmailRequestDto emailRequestDto, @AuthenticationPrincipal UserDto user) {
        log.debug("User with ID {} requested email change", user.getId());

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


    @PostMapping(value = "/change/confirm")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> emailConformation(@RequestBody final TokenRequestDto tokenRequestDto) {
        log.debug("New email conformation");

        String response = emailChangingService.confirmEmailChangingToken(tokenRequestDto.getToken());

        log.debug("New successfully confirmed");
        return ResponseEntity.ok(response);
    }
}
