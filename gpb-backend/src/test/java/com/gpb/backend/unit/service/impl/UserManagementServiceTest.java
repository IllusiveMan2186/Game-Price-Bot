package com.gpb.backend.unit.service.impl;

import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.repository.WebUserRepository;
import com.gpb.backend.service.impl.UserManagementServiceImpl;
import com.gpb.common.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserManagementServiceTest {

    @Mock
    private WebUserRepository webUserRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserManagementServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById_whenSuccess_shouldReturnUserDto() {
        long userId = 1L;
        WebUser webUser = new WebUser();
        UserDto userDto = new UserDto("username", "password", "token", "role", "ua");
        when(webUserRepository.findById(userId)).thenReturn(Optional.of(webUser));
        when(modelMapper.map(webUser, UserDto.class)).thenReturn(userDto);


        UserDto result = userService.getUserById(userId);


        assertNotNull(result);
        verify(webUserRepository).findById(userId);
        verify(modelMapper).map(webUser, UserDto.class);
    }

    @Test
    void testGetUserByBasicUserId_whenSuccess_shouldReturnUser() {
        long basicUserId = 1L;
        WebUser webUser = new WebUser();
        when(webUserRepository.findByBasicUserId(basicUserId)).thenReturn(Optional.of(webUser));


        WebUser result = userService.getUserByBasicUserId(basicUserId);


        assertEquals(webUser, result);
        verify(webUserRepository).findByBasicUserId(basicUserId);
    }

    @Test
    void testGetUserByBasicUserId_whenUserNotFound_shouldThrowNotFoundException() {
        long basicUserId = 1L;

        when(webUserRepository.findByBasicUserId(basicUserId)).thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserByBasicUserId(basicUserId)
        );


        assertEquals("app.user.error.id.not.found", exception.getMessage());
    }


    @Test
    void testGetWebUserByEmail_whenSuccess_shouldReturnWebUser() {
        String email = "email";
        WebUser webUser = new WebUser();
        when(webUserRepository.findByEmail(email)).thenReturn(Optional.of(webUser));


        WebUser result = userService.getWebUserByEmail(email);


        assertEquals(webUser, result);
        verify(webUserRepository).findByEmail(email);
    }

    @Test
    void testGetWebUserByEmail_whenUserNotFound_shouldThrowNotFoundException() {
        String email = "email";

        when(webUserRepository.findByEmail(email)).thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getWebUserByEmail(email)
        );


        assertEquals("app.user.error.email.not.found", exception.getMessage());
    }

    @Test
    void testGetUserById_whenUserNotFound_shouldThrowNotFoundException() {
        long userId = 1L;
        when(webUserRepository.findById(userId)).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void testUpdateLocale_whenSuccess_shouldSaveNewLocale() {
        WebUser webUser = new WebUser();
        long webUserId = 1L;
        String newLocale = "ua";
        when(webUserRepository.findById(webUserId)).thenReturn(Optional.of(webUser));


        userService.updateLocale(newLocale, webUserId);


        webUser.setLocale(new Locale(newLocale));
        verify(webUserRepository).save(webUser);
    }

    @Test
    void testActivateUser_whenSuccess_shouldSaveNewUser() {
        WebUser webUser = new WebUser();
        long webUserId = 1L;
        when(webUserRepository.findById(webUserId)).thenReturn(Optional.of(webUser));


        userService.activateUser(webUserId);


        webUser.setActivated(true);
        verify(webUserRepository).save(webUser);
    }

    @Test
    void testGetWebUsers_whenSuccess_shouldReturnWebUsers() {
        List<Long> userIds = new ArrayList<>();
        userIds.add(1L);
        userIds.add(2L);
        List<WebUser> userList = new ArrayList<>();
        when(webUserRepository.findAllByIdIn(userIds)).thenReturn(userList);


        List<WebUser> result = userService.getWebUsers(userIds);


        assertEquals(userList, result);
        verify(webUserRepository).findAllByIdIn(userIds);
    }

    @Test
    void testSetBasicUserId_whenSuccess_shouldCallUserBasicChange() {
        long currentBasicUserId = 123L;
        long newBasicUserId = 456L;


        userService.setBasicUserId(currentBasicUserId, newBasicUserId);


        verify(webUserRepository, times(1)).updateBasicUserIdByBasicUserId(currentBasicUserId, newBasicUserId);
    }
}
