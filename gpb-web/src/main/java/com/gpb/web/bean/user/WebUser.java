package com.gpb.web.bean.user;

import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "userId")
public class WebUser extends BasicUser {

    private String email;

    @ToString.Exclude
    private String password;

    private boolean isActivated;

    private boolean isLocked;

    private int failedAttempt;

    private Date lockTime;

    private String role;

    public void increaseFailedAttempt() {
        failedAttempt++;
    }
}
