package com.gpb.web.service.impl;

import com.gpb.web.bean.user.Credentials;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.exception.EmailAlreadyExistException;
import com.gpb.web.exception.LoginFailedException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.exception.UserDataNotChangedException;
import com.gpb.web.exception.UserLockedException;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Calendar;
import java.util.Date;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final int MAX_FAILED_ATTEMPTS = 3;

    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    private final WebUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    public UserServiceImpl(WebUserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto getUserById(long userId) {
        return modelMapper.map(getWebUserById(userId), UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(final String email) {
        log.info(String.format("Get user by email : %s", email));

        final WebUser user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(
                "app.user.error.email.not.found"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto createUser(final UserRegistration userRegistration) {
        log.info(String.format("Create user : %s", userRegistration.getEmail()));
        if (userRepository.findByEmail(userRegistration.getEmail()).isPresent()) {
            log.info(String.format("User with email : '%s' already registered", userRegistration.getEmail()));
            throw new EmailAlreadyExistException();
        }
        WebUser user = getWebUser(userRegistration);
        user = userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUserEmail(String newEmail, UserDto user) {
        log.info(String.format("Update email for user : %s", user.getId()));

        if (newEmail.equals(user.getEmail())) {
            log.info(String.format("User with id : '%s' did not changed email for update", user.getId()));
            throw new UserDataNotChangedException();
        } else if (userRepository.findByEmail(newEmail).isPresent()) {
            log.info(String.format("User with email : '%s' already registered", newEmail));
            throw new EmailAlreadyExistException();
        }

        WebUser webUser = getWebUserById(user.getId());
        webUser.setEmail(newEmail);
        WebUser updatedUser = userRepository.save(webUser);
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
        WebUser updatedUser = userRepository.save(webUser);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public void subscribeToGame(long userId, long gameId) {
        log.info(String.format("Subscribe for game(%s) into user(%s) game list", gameId, userId));
        userRepository.addGameToUserListOfGames(userId, gameId);
    }

    @Override
    public void unsubscribeFromGame(long userId, long gameId) {
        log.info(String.format("Unsubscribe game(%s) from user(%s) game list", gameId, userId));
        userRepository.removeGameFromUserListOfGames(userId, gameId);
    }

    public UserDto login(Credentials credentials) {
        log.info(String.format("Login user : %s", credentials.getEmail()));
        final WebUser user = userRepository.findByEmail(credentials.getEmail())
                .orElseThrow(LoginFailedException::new);

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
            unlockUser(user);
            return modelMapper.map(user, UserDto.class);
        }
        failedLoginAttempt(user);
        throw new LoginFailedException();
    }

    private void failedLoginAttempt(WebUser user) {
        user.increaseFailedAttempt();
        if (user.getFailedAttempt() >= MAX_FAILED_ATTEMPTS) {
            lockUser(user);
        }
        userRepository.save(user);
        log.info(String.format("Failed login for user : '%s'", user.getEmail()));
    }

    private void lockUser(WebUser user) {
        user.setLocked(true);

        Calendar lockTime = Calendar.getInstance();
        lockTime.setTime(new Date());
        lockTime.add(Calendar.DATE, 1);
        user.setLockTime(lockTime.getTime());
        log.info(String.format("Unlock user : '%s'", user.getEmail()));
    }

    private void unlockUser(WebUser user) {
        user.setLocked(false);
        user.setLockTime(null);
        user.setFailedAttempt(0);
        userRepository.save(user);
        log.info(String.format("Lock user : '%s'", user.getEmail()));
    }

    private WebUser getWebUser(UserRegistration userRegistration) {
        return WebUser.builder()
                .email(userRegistration.getEmail())
                .password(passwordEncoder.encode(CharBuffer.wrap(userRegistration.getPassword()))).build();
    }

    private boolean matchPassword(char[] decodedPassword, String encodedPassword) {
        return passwordEncoder.matches(CharBuffer.wrap(decodedPassword), encodedPassword);
    }

    private WebUser getWebUserById(final long userId) {
        log.info(String.format("Get user by id : %s", userId));

        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("app.user.error.id.not.found"));
    }
}
