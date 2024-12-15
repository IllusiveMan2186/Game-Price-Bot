package com.gpb.game.bean.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountLinkerEvent {

    private String token;

    private long sourceUserId;
}
