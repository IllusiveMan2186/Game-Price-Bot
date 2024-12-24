package com.gpb.common.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event for user accounts linking
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountLinkerEvent {

    /**
     * Token for linking
     */
    private String token;

    /**
     * Basic id of source user
     */
    private long sourceBasicUserId;
}
