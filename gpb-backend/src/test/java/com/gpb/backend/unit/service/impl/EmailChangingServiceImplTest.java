package com.gpb.backend.unit.service.impl;

import com.gpb.backend.entity.EmailChanging;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.exception.TokenExpireException;
import com.gpb.backend.repository.EmailChangingRepository;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.service.impl.EmailChangingServiceImpl;
import com.gpb.common.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailChangingServiceImplTest {

    @Mock
    private UserAuthenticationService userActivationService;

    @Mock
    private EmailChangingRepository emailChangingRepository;

    @InjectMocks
    private EmailChangingServiceImpl emailChangingService;

    private WebUser user;
    private EmailChanging emailChanging;

    @BeforeEach
    void setUp() {
        user = new WebUser();
        user.setId(1L);

        emailChanging = EmailChanging.builder()
                .newEmail("new@example.com")
                .user(user)
                .oldEmailToken(UUID.randomUUID().toString())
                .newEmailToken(UUID.randomUUID().toString())
                .expirationTime(LocalDateTime.now().plusHours(1))
                .build();
    }

    @Test
    void testCreateEmailChanging_whenSuccess_shouldCreateNewEmailChangingRequest() {
        when(emailChangingRepository.save(any(EmailChanging.class))).thenReturn(emailChanging);

        EmailChanging result = emailChangingService.createEmailChanging("new@example.com", user);

        assertNotNull(result);
        assertEquals("new@example.com", result.getNewEmail());
        assertNotNull(result.getOldEmailToken());
        assertNotNull(result.getNewEmailToken());
        verify(emailChangingRepository, times(1)).save(any(EmailChanging.class));
    }

    @Test
    void testConfirmChangingToken_whenNewEmailConfirm_shouldSaveChangedRequestWithOneConfirmation() {
        when(emailChangingRepository.findByNewEmailToken(emailChanging.getNewEmailToken()))
                .thenReturn(Optional.of(emailChanging));

        String response = emailChangingService.confirmEmailChangingToken(emailChanging.getNewEmailToken());

        assertEquals("app.email.change.confirm.success.message", response);
        assertTrue(emailChanging.isNewEmailConfirmed());
        verify(emailChangingRepository, times(1)).save(emailChanging);
    }

    @Test
    void testConfirmChangingToken_whenNotFound_shouldThrowException() {
        when(emailChangingRepository.findByNewEmailToken(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> emailChangingService.confirmEmailChangingToken("invalidToken"));
    }

    @Test
    void testConfirmChangingToken_whenOldEmailConfirm_shouldSaveChangedRequestWithOneConfirmation() {
        when(emailChangingRepository.findByOldEmailToken(emailChanging.getOldEmailToken()))
                .thenReturn(Optional.of(emailChanging));


        String response = emailChangingService.confirmEmailChangingToken(emailChanging.getOldEmailToken());


        assertEquals("app.email.change.confirm.success.message", response);
        assertTrue(emailChanging.isOldEmailConfirmed());
        verify(emailChangingRepository, times(1)).save(emailChanging);
    }

    @Test
    void testConfirmOldEmailToken_whenNotFound_shouldThrowException() {
        when(emailChangingRepository.findByOldEmailToken(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> emailChangingService.confirmEmailChangingToken("invalidToken"));
    }

    @Test
    void testUpdateEmailChangingStatus_whenExpiredToken() {
        when(emailChangingRepository.findByOldEmailToken(emailChanging.getOldEmailToken()))
                .thenReturn(Optional.of(emailChanging));

        emailChanging.setExpirationTime(LocalDateTime.now().minusMinutes(1));


        TokenExpireException exception = assertThrows(
                TokenExpireException.class,
                () -> emailChangingService.confirmEmailChangingToken(emailChanging.getOldEmailToken()));


        assertEquals("app.email.change.token.expire", exception.getMessage());
        verify(emailChangingRepository, times(1)).deleteById(emailChanging.getId());
    }

    @Test
    void testUpdateEmailChangingStatus_whenConfirmedBothTokens_shouldSaveUpdateEmail() {
        when(emailChangingRepository.findByOldEmailToken(emailChanging.getOldEmailToken()))
                .thenReturn(Optional.of(emailChanging));
        emailChanging.setOldEmailConfirmed(true);
        emailChanging.setNewEmailConfirmed(true);


        String response = emailChangingService.confirmEmailChangingToken(emailChanging.getOldEmailToken());


        assertEquals("app.email.change.success.message", response);
        verify(userActivationService, times(1)).updateUserEmail(emailChanging.getNewEmail(), emailChanging.getUser());
        verify(emailChangingRepository, times(1)).deleteById(emailChanging.getId());
    }
}

