package com.gpb.backend.service.impl;

import com.gpb.backend.entity.WebUser;
import com.gpb.backend.repository.WebUserRepository;
import com.gpb.backend.service.UserManagementService;
import com.gpb.common.exception.NotFoundException;
import com.gpb.common.service.ChangeUserBasicIdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
public class UserManagementServiceImpl implements UserManagementService, ChangeUserBasicIdService {

    private final WebUserRepository webUserRepository;

    public UserManagementServiceImpl(WebUserRepository webUserRepository) {
        this.webUserRepository = webUserRepository;
    }

    @Override
    public WebUser getWebUserById(final long userId) {
        log.info("Get user by id : {}", userId);
        return webUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("app.user.error.id.not.found"));
    }

    @Override
    public WebUser getUserByBasicUserId(long basicUserId) {
        return webUserRepository.findByBasicUserId(basicUserId)
                .orElseThrow(() -> new NotFoundException("app.user.error.id.not.found"));
    }

    @Override
    public Optional<WebUser> getWebUserByEmail(String email) {
        log.info("Get web user by email : {}", email);
        return webUserRepository.findByEmail(email);
    }


    @Override
    @SuppressWarnings("deprecation")
    public void updateLocale(String locale, long userId) {
        log.info("Change locale for user '{}' into '{}'", userId, locale);

        WebUser webUser = getWebUserById(userId);
        webUser.setLocale(new Locale(locale));

        webUserRepository.save(webUser);
    }

    @Override
    public void activateUser(long userId) {
        log.info("Activating user account.");

        WebUser user = getWebUserById(userId);
        user.activate();
        webUserRepository.save(user);
    }

    @Override
    public void setBasicUserId(long currentBasicUserId, long newBasicUserId) {
        webUserRepository.updateBasicUserIdByBasicUserId(currentBasicUserId, newBasicUserId);
    }
}
