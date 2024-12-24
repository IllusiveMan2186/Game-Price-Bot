package com.gpb.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Locale;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class WebUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long basicUserId;

    private String email;

    @ToString.Exclude
    private String password;

    private boolean isActivated;

    private boolean isLocked;

    private int failedAttempt;

    private Date lockTime;

    private String role;

    private Locale locale;

    /**
     * Activates the user account.
     */
    public void activate() {
        this.isActivated = true;
    }

    /**
     * Locks the user account and sets the lock time to the current time.
     */
    public void lockAccount() {
        this.isLocked = true;
        this.lockTime = new Date(); // Lock time is set to current time
    }

    /**
     * Unlocks the user account, resets lock time and failed attempts.
     */
    public void unlockAccount() {
        this.isLocked = false;
        this.lockTime = null;
        this.failedAttempt = 0; // Reset failed attempts
    }

    /**
     * Increments the failed login attempts and locks the account if max attempts are reached.
     *
     * @param maxAttempts Maximum allowed failed attempts
     */
    public void incrementFailedAttempts(int maxAttempts) {
        this.failedAttempt++;
        if (this.failedAttempt >= maxAttempts) {
            lockAccount();
        }
    }

    /**
     * Checks if the account is currently locked.
     *
     * @return true if the account is locked, false otherwise
     */
    public boolean isAccountLocked() {
        return this.isLocked;
    }

    /**
     * Validates the given raw password against the stored encoded password.
     *
     * @param rawPassword    The raw password
     * @param passwordEncoder The encoder used to validate the password
     * @return true if the password is valid, false otherwise
     */
    public boolean isPasswordValid(CharSequence rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.password);
    }
}
