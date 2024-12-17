package com.gpb.backend.service.impl;

import com.gpb.backend.bean.user.Credentials;
import com.gpb.backend.bean.user.UserDto;
import com.gpb.backend.bean.user.UserRegistration;
import com.gpb.backend.bean.user.WebUser;
import com.gpb.backend.exception.EmailAlreadyExistException;
import com.gpb.backend.exception.LoginFailedException;
import com.gpb.backend.exception.NotFoundException;
import com.gpb.backend.exception.UserDataNotChangedException;
import com.gpb.backend.exception.UserLockedException;
import com.gpb.backend.exception.UserNotActivatedException;
import com.gpb.backend.repository.WebUserRepository;
import com.gpb.backend.rest.RestTemplateHandler;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.util.Constants;
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
    private final RestTemplateHandler restTemplateHandler;

    public UserAuthenticationServiceImpl(WebUserRepository webUserRepository,
                                         PasswordEncoder passwordEncoder,
                                         ModelMapper modelMapper,
                                         RestTemplateHandler restTemplateHandler) {
        this.webUserRepository = webUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.restTemplateHandler = restTemplateHandler;
    }

    @Override
    public UserDto login(Credentials credentials) {
        log.info("Login attempt for user with email.");

        WebUser user = webUserRepository.findByEmail(credentials.getEmail())
                .orElseThrow(LoginFailedException::new);

        if (!user.isActivated()) {
            throw new UserNotActivatedException();
        }

        if (user.isAccountLocked() && !isLockTimeOver(user.getLockTime())) {
            throw new UserLockedException();
        }

        if (user.isPasswordValid(CharBuffer.wrap(credentials.getPassword()), passwordEncoder)) {
            user.unlockAccount();
            webUserRepository.save(user);
            return modelMapper.map(user, UserDto.class);
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

        Long basicUserId = restTemplateHandler.executeRequest("/user", HttpMethod.POST, null, Long.class);

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
    public UserDto getUserByEmail(final String email) {
        log.info("Get user by email : {}", email);
        final WebUser user = webUserRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("app.user.error.email.not.found"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUserEmail(String newEmail, UserDto userDto) {
        log.info("Updating email for user.");

        if (newEmail.equals(userDto.getEmail())) {
            throw new UserDataNotChangedException();
        }

        if (webUserRepository.findByEmail(newEmail).isPresent()) {
            throw new EmailAlreadyExistException();
        }

        WebUser user = getWebUserById(userDto.getId());
        user.setEmail(newEmail);
        webUserRepository.save(user);

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUserPassword(char[] password, UserDto userDto) {
        log.info("Updating password for user.");

        WebUser user = getWebUserById(userDto.getId());

        if (user.isPasswordValid(CharBuffer.wrap(password), passwordEncoder)) {
            throw new UserDataNotChangedException();
        }

        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(password)));
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
