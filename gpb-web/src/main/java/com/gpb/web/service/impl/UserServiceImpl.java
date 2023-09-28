package com.gpb.web.service.impl;

import com.gpb.web.bean.WebUser;
import com.gpb.web.exception.EmailAlreadyExistException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final WebUserRepository userRepository;

    public UserServiceImpl(WebUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public WebUser getUserById(final long userId) {
        log.info(String.format("Get user by id : %s", userId));

        final WebUser user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("User with id '%s' not found", userId));
        }
        return user;
    }

    @Override
    public WebUser getUserByEmail(final String email) {
        log.info(String.format("Get user by email : %s", email));

        final WebUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException(String.format("User with email '%s' not found", email));
        }
        return user;
    }

    @Override
    public WebUser createUser(final WebUser user) {
        log.info(String.format("Create user : %s", user.getUsername()));
        if (userRepository.findByEmail(user.getUsername()) != null) {
            throw new EmailAlreadyExistException();
        }
        return userRepository.save(user);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(String.format("Login user : %s", username));
        return getUserByEmail(username);
    }
}
