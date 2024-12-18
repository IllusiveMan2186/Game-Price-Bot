package com.gpb.backend.service.impl;

import com.gpb.backend.bean.user.WebUser;
import com.gpb.backend.bean.user.dto.UserDto;
import com.gpb.backend.exception.NotFoundException;
import com.gpb.backend.repository.WebUserRepository;
import com.gpb.backend.service.UserManagementService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    private final WebUserRepository webUserRepository;
    private final ModelMapper modelMapper;

    @Value("${GAME_SERVICE_URL}")
    private String gameServiceUrl;

    public UserManagementServiceImpl(WebUserRepository webUserRepository,
                                     ModelMapper modelMapper) {
        this.webUserRepository = webUserRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto getUserById(long userId) {
        return modelMapper.map(getWebUserById(userId), UserDto.class);
    }

    @Override
    public WebUser getUserByBasicUserId(long basicUserId) {
        return webUserRepository.findByBasicUserId(basicUserId)
                .orElseThrow(() -> new NotFoundException("app.user.error.id.not.found"));
    }

    @Override
    public WebUser getWebUserByEmail(String email) {
        log.info("Get web user by email : {}", email);
        return webUserRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("app.user.error.email.not.found"));
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
    public List<WebUser> getWebUsers(List<Long> userIds) {
        return webUserRepository.findAllByIdIn(userIds);
    }

    private WebUser getWebUserById(final long userId) {
        log.info("Get user by id : {}", userId);

        return webUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("app.user.error.id.not.found"));
    }

    @Override
    public void activateUser(long userId) {
        log.info("Activating user account.");

        WebUser user = getWebUserById(userId);
        user.activate();
        webUserRepository.save(user);
    }

}
