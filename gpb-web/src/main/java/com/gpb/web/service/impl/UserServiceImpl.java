package com.gpb.web.service.impl;

import com.gpb.web.bean.WebUser;
import com.gpb.web.exception.EmailAlreadyExistException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    //private static final Log log = LogFactory.getLog(UserServiceImpl.class.getName());

    private final WebUserRepository userRepository;

    public UserServiceImpl( final WebUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public WebUser getUserById(final long userId) {
        final WebUser user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("User with id '%s' not found", userId));
        }

        //log.info(String.format("Get user by id:%s", userId));
        return user;
    }

    @Override
    public WebUser getUserByEmail(final String email) {
        final WebUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException(String.format("User with email '%s' not found", email));
        }

        //log.info(String.format("Get user by email:%s", email));
        return user;
    }

    @Override
    public WebUser getUserByUsername(final String username) {
        final WebUser user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException(String.format("User with username '%s' not found", username));
        }

        //log.info(String.format("Get user by username:%s", username));
        return user;
    }

    @Override
    public WebUser createUser(final WebUser user) {
        if(userRepository.findByEmail(user.getEmail()) != null){
            throw new EmailAlreadyExistException();
        }
        //log.info(String.format("Create user:%s", user));

        return userRepository.save(user);
    }


    @Override
    public boolean deleteUser(final long userId) {
        //log.info(String.format("Delete user with id:%s", userId));

        if (userRepository.findById(userId) == null) {
            return false;
        }

        userRepository.deleteById(userId);

        return true;
    }
}
