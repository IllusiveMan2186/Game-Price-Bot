package com.gpb.web.service.impl;

import com.gpb.web.bean.user.BasicUser;
import com.gpb.web.bean.user.Credentials;
import com.gpb.web.bean.user.WebMessengerConnector;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.exception.EmailAlreadyExistException;
import com.gpb.web.exception.LoginFailedException;
import com.gpb.web.exception.NotExistingMessengerActivationTokenException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.exception.UserDataNotChangedException;
import com.gpb.web.exception.UserLockedException;
import com.gpb.web.exception.UserNotActivatedException;
import com.gpb.web.repository.WebMessengerConnectorRepository;
import com.gpb.web.repository.UserRepository;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 86_400_000; // 24 hours
    private static final String USER_ROLE = "ROLE_USER";

    private final UserRepository userRepository;

    private final WebUserRepository webUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final WebMessengerConnectorRepository messengerConnectorRepository;

    public UserServiceImpl(UserRepository userRepository, WebUserRepository webUserRepository,
                           PasswordEncoder passwordEncoder, ModelMapper modelMapper,
                           WebMessengerConnectorRepository messengerConnectorRepository) {
        this.userRepository = userRepository;
        this.webUserRepository = webUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.messengerConnectorRepository = messengerConnectorRepository;
    }

    @Override
    public UserDto getUserById(long userId) {
        return modelMapper.map(getWebUserById(userId), UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(final String email) {
        log.info(String.format("Get user by email : %s", email));

        final WebUser user = webUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(
                "app.user.error.email.not.found"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public WebUser getWebUserByEmail(String email) {
        log.info(String.format("Get web user by email : %s", email));

        return webUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(
                "app.user.error.email.not.found"));
    }

    @Override
    public WebUser createUser(final UserRegistration userRegistration) {
        log.info(String.format("Create user : %s", userRegistration.getEmail()));
        if (webUserRepository.findByEmail(userRegistration.getEmail()).isPresent()) {
            log.info(String.format("User with email : '%s' already registered", userRegistration.getEmail()));
            throw new EmailAlreadyExistException();
        }
        BasicUser basicUser = new BasicUser();
        basicUser = userRepository.save(basicUser);
        WebUser user = getWebUser(userRegistration);
        user.setActivated(false);
        user.setBasicUser(basicUser);
        return webUserRepository.save(user);
    }

    @Override
    public UserDto updateUserEmail(String newEmail, UserDto user) {
        log.info(String.format("Update email for user : %s", user.getId()));

        if (newEmail.equals(user.getEmail())) {
            log.info(String.format("User with id : '%s' did not changed email for update", user.getId()));
            throw new UserDataNotChangedException();
        } else if (webUserRepository.findByEmail(newEmail).isPresent()) {
            log.info(String.format("User with email : '%s' already registered", newEmail));
            throw new EmailAlreadyExistException();
        }

        WebUser webUser = getWebUserById(user.getId());
        webUser.setEmail(newEmail);
        WebUser updatedUser = webUserRepository.save(webUser);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public UserDto updateUserPassword(char[] password, UserDto user) {
        log.info(String.format("Update password for user : %s", user.getId()));

        WebUser webUser = getWebUserById(user.getId());
        if (matchPassword(password, webUser.getPassword())) {
            log.info(String.format("User with id : '%s' did not changed password for update", user.getId()));
            throw new UserDataNotChangedException();
        }

        webUser.setPassword(passwordEncoder.encode(CharBuffer.wrap(password)));
        WebUser updatedUser = webUserRepository.save(webUser);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    public UserDto login(Credentials credentials) {
        log.info(String.format("Login user : %s", credentials.getEmail()));
        final WebUser user = webUserRepository.findByEmail(credentials.getEmail())
                .orElseThrow(LoginFailedException::new);

        if (!user.isActivated()) {
            throw new UserNotActivatedException();
        }

        if (user.isLocked()) {
            long lockTimeInMillis = user.getLockTime().getTime();
            long currentTimeInMillis = System.currentTimeMillis();
            if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
                unlockUser(user);
            } else {
                throw new UserLockedException();
            }
        }

        if (matchPassword(credentials.getPassword(), user.getPassword())) {
            if (user.getFailedAttempt() > 0)
                unlockUser(user);
            return modelMapper.map(user, UserDto.class);
        }
        failedLoginAttempt(user);
        throw new LoginFailedException();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void updateLocale(String locale, long userId) {
        log.info(String.format("Change locale for user '%s' into '%s'", userId, locale));

        WebUser webUser = getWebUserById(userId);
        webUser.setLocale(new Locale(locale));

        webUserRepository.save(webUser);
    }

    @Override
    public void activateUser(Long userId) {
        log.info(String.format("Activate user '%s'", userId));

        WebUser webUser = getWebUserById(userId);
        webUser.setActivated(true);
        webUserRepository.save(webUser);
    }

    @Override
    public void connectTelegramUser(String token, long webUserId) {

        WebMessengerConnector connector = messengerConnectorRepository.findById(token)
                .orElseThrow(NotExistingMessengerActivationTokenException::new);
        BasicUser user = userRepository.findById(connector.getUserId());
        WebUser webUser = getWebUserById(webUserId);
        BasicUser oldUSer = webUser.getBasicUser();

        user.getGameList().addAll(oldUSer.getGameList());
        user.getNotificationTypes().addAll(oldUSer.getNotificationTypes());
        webUser.setBasicUser(user);

        webUserRepository.save(webUser);
        messengerConnectorRepository.deleteById(token);
        userRepository.deleteById(oldUSer.getId());
    }

    @Override
    public String getTelegramUserConnectorToken(long webUserId) {
        WebUser webUser = getWebUserById(webUserId);
        WebMessengerConnector connector = WebMessengerConnector.builder()
                .userId(webUser.getBasicUser().getId())
                .build();
        return messengerConnectorRepository.save(connector).getToken();
    }

    @Override
    public List<WebUser> getWebUsers(List<Long> userIds) {
        return webUserRepository.findAllByIdIn(userIds);
    }

    private void failedLoginAttempt(WebUser user) {
        user.increaseFailedAttempt();
        if (user.getFailedAttempt() >= MAX_FAILED_ATTEMPTS) {
            lockUser(user);
        }
        webUserRepository.save(user);
        log.info(String.format("Failed login for user : '%s'", user.getEmail()));
    }

    private void lockUser(WebUser user) {
        user.setLocked(true);

        Calendar lockTime = Calendar.getInstance();
        lockTime.setTime(new Date());
        lockTime.add(Calendar.DATE, 1);
        user.setLockTime(lockTime.getTime());
        log.info(String.format("Lock user : '%s'", user.getEmail()));
    }

    private void unlockUser(WebUser user) {
        user.setLocked(false);
        user.setLockTime(null);
        user.setFailedAttempt(0);
        webUserRepository.save(user);
        log.info(String.format("Unlock user : '%s'", user.getEmail()));
    }

    private WebUser getWebUser(UserRegistration userRegistration) {
        return WebUser.builder()
                .email(userRegistration.getEmail())
                .password(passwordEncoder.encode(CharBuffer.wrap(userRegistration.getPassword())))
                .role(USER_ROLE)
                .locale(new Locale(userRegistration.getLocale()))
                .build();
    }

    private boolean matchPassword(char[] decodedPassword, String encodedPassword) {
        return passwordEncoder.matches(CharBuffer.wrap(decodedPassword), encodedPassword);
    }

    private WebUser getWebUserById(final long userId) {
        log.info(String.format("Get user by id : %s", userId));

        return webUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("app.user.error.id.not.found"));
    }
}
