package com.gpb.backend.service.impl;

import com.gpb.backend.entity.Credentials;
import com.gpb.backend.entity.UserRegistration;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.exception.EmailAlreadyExistException;
import com.gpb.backend.exception.LoginFailedException;
import com.gpb.backend.exception.UserDataNotChangedException;
import com.gpb.backend.exception.UserLockedException;
import com.gpb.backend.exception.UserNotActivatedException;
import com.gpb.backend.exception.WrongPasswordException;
import com.gpb.backend.repository.WebUserRepository;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.util.Constants;
import com.gpb.common.entity.user.NotificationRequestDto;
import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.common.exception.NotFoundException;
import com.gpb.common.service.RestTemplateHandlerService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Service
@Log4j2
@Data
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final WebUserRepository webUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final RestTemplateHandlerService restTemplateHandler;

    public UserAuthenticationServiceImpl(WebUserRepository webUserRepository,
                                         PasswordEncoder passwordEncoder,
                                         ModelMapper modelMapper,
                                         RestTemplateHandlerService restTemplateHandler) {
        this.webUserRepository = webUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.restTemplateHandler = restTemplateHandler;
    }

    @Override
    public WebUser login(Credentials credentials) {
        log.debug("Login attempt for user with email.");

        WebUser user = webUserRepository.findByEmail(credentials.getEmail())
                .orElseThrow(LoginFailedException::new);

        if (!user.isActivated()) {
            log.warn("Login failed for user {}: account not activated.", user.getId());
            throw new UserNotActivatedException();
        }

        if (user.isAccountLocked() && !isLockTimeOver(user.getLockTime())) {
            log.warn("Login failed for user {}: account locked.", user.getId());
            throw new UserLockedException();
        }

        if (user.isPasswordValid(CharBuffer.wrap(credentials.getPassword()), passwordEncoder)) {
            if (user.isAccountLocked() && isLockTimeOver(user.getLockTime())) {
                log.warn("Unlock locked user {}.", user.getId());
                user.unlockAccount();
                return webUserRepository.save(user);
            } else {
                log.debug("User {} logged in.", user.getId());
                return user;
            }
        }

        user.incrementFailedAttempts(Constants.MAX_FAILED_ATTEMPTS);
        webUserRepository.save(user);
        throw new LoginFailedException();
    }

    @Override
    public WebUser createUser(UserRegistration userRegistration) {
        log.info("Registering new user.");

        if (webUserRepository.findByEmail(userRegistration.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException();
        }

        Long basicUserId = restTemplateHandler.executeRequestWithBody("/user",
                HttpMethod.POST,
                null,
                new NotificationRequestDto(UserNotificationType.EMAIL),
                Long.class);

        WebUser user = WebUser.builder()
                .email(userRegistration.getEmail())
                .password(passwordEncoder.encode(CharBuffer.wrap(userRegistration.getPassword())))
                .role(Constants.USER_ROLE)
                .locale(new Locale(userRegistration.getLocale()))
                .basicUserId(basicUserId)
                .isActivated(false)
                .build();

        return webUserRepository.save(user);
    }

    @Override
    public UserDto getUserById(long userId) {
        log.debug("Get user by id - {}", userId);
        return modelMapper.map(getWebUserById(userId), UserDto.class);
    }

    @Override
    public UserDto updateUserEmail(String newEmail, WebUser user) {
        log.info("Updating email for user.");

        user.setEmail(newEmail);
        webUserRepository.save(user);

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUserPassword(char[] oldPassword, char[] newPassword, UserDto userDto) {
        log.info("Updating password for user.");

        WebUser user = getWebUserById(userDto.getId());

        if (!user.isPasswordValid(CharBuffer.wrap(oldPassword), passwordEncoder)) {
            throw new WrongPasswordException();
        }

        if (user.isPasswordValid(CharBuffer.wrap(newPassword), passwordEncoder)) {
            throw new UserDataNotChangedException();
        }

        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(newPassword)));
        webUserRepository.save(user);

        return modelMapper.map(user, UserDto.class);
    }

    private boolean isLockTimeOver(Date lockTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lockTime);
        return System.currentTimeMillis() > calendar.getTimeInMillis() + Constants.LOCK_TIME_DURATION;
    }

    private WebUser getWebUserById(long userId) {
        return webUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("app.user.error.id.not.found"));
    }
}
