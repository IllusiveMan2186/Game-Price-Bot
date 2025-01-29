package com.gpb.common.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkUsersEvent {

    private String token;
    private long currentUserBasicId;
}
