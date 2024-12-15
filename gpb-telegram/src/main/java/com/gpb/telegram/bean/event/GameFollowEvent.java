package com.gpb.telegram.bean.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameFollowEvent {

    long userId;
    long gameId;
}
