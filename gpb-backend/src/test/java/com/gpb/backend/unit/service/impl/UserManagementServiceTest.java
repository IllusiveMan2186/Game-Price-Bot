package com.gpb.backend.unit.service.impl;

import com.gpb.backend.entity.WebUser;
import com.gpb.backend.repository.WebUserRepository;
import com.gpb.backend.service.impl.UserManagementServiceImpl;
import com.gpb.common.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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


        Optional<WebUser> result = userService.getWebUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(webUser, result.get());
        verify(webUserRepository).findByEmail(email);
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
    void testSetBasicUserId_whenSuccess_shouldCallUserBasicChange() {
        long currentBasicUserId = 123L;
        long newBasicUserId = 456L;


        userService.setBasicUserId(currentBasicUserId, newBasicUserId);


        verify(webUserRepository, times(1)).updateBasicUserIdByBasicUserId(currentBasicUserId, newBasicUserId);
    }
}
