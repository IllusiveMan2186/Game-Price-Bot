package com.gpb.web.service.impl;

import com.gpb.web.bean.user.Credentials;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.exception.EmailAlreadyExistException;
import com.gpb.web.exception.InvalidPasswordException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.exception.UserDataNotChangedException;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final WebUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(WebUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto getUserById(long userId) {
        return new UserDto(getWebUserById(userId));
    }

    @Override
    public UserDto getUserByEmail(final String email) {
        log.info(String.format("Get user by email : %s", email));

        final WebUser user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(String
                .format("User with email '%s' not found", email)));
        return new UserDto(user);
    }

    @Override
    public UserDto createUser(final UserRegistration userRegistration) {
        log.info(String.format("Create user : %s", userRegistration.getUsername()));
        if (userRepository.findByEmail(userRegistration.getUsername()).isPresent()) {
            throw new EmailAlreadyExistException();
        }
        WebUser user = getWebUser(userRegistration);
        user = userRepository.save(user);
        return new UserDto(user);
    }

    @Override
    public UserDto updateUser(UserRegistration newUserRegistration, long userId) {
        log.info(String.format("Update user : %s", userId));

        WebUser oldUser = getWebUserById(userId);
        WebUser newUser = getWebUser(newUserRegistration);
        newUser.setId(userId);

        if (equalsUpdatedUser(oldUser, newUserRegistration)) {
            throw new UserDataNotChangedException();
        } else if (!newUser.getEmail().equals(oldUser.getEmail()) && userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException();
        }
        WebUser user = userRepository.save(newUser);
        return new UserDto(user);
    }

    @Override
    public void addGameToUserListOfGames(long userId, long gameId) {
        log.info(String.format("Add game(%s) into user(%s) game list", userId, gameId));
        userRepository.addGameToUserListOfGames(userId, gameId);
    }

    public UserDto login(Credentials credentials) {
        log.info(String.format("Login user : %s", credentials.getUsername()));
        final WebUser user = userRepository.findByEmail(credentials.getUsername())
                .orElseThrow(() -> new NotFoundException(String
                        .format("User with email '%s' not found", credentials.getUsername())));

        if (matchPassword(credentials.getPassword(), user.getPassword())) {
            return new UserDto(user);
        }
        throw new InvalidPasswordException();
    }

    private WebUser getWebUser(UserRegistration userRegistration) {
        return WebUser.builder()
                .email(userRegistration.getUsername())
                .password(passwordEncoder.encode(CharBuffer.wrap(userRegistration.getPassword()))).build();
    }

    private boolean equalsUpdatedUser(WebUser oldUser, UserRegistration newUser) {
        return oldUser.getEmail().equals(newUser.getUsername())
                && matchPassword(newUser.getPassword(), oldUser.getPassword());
    }

    private boolean matchPassword(char[] decodedPassword, String encodedPassword) {
        return passwordEncoder.matches(CharBuffer.wrap(decodedPassword), encodedPassword);
    }

    private WebUser getWebUserById(final long userId) {
        log.info(String.format("Get user by id : %s", userId));

        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String
                        .format("User with id '%s' not found", userId)));
    }
}
