package com.gpb.common.entity.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDto {

    @NotEmpty
    private UserNotificationType userNotificationType;
}
